package com.ww.dtable.dao.impl

import com.google.inject.{Inject, Singleton}
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.api.{DayType, HolidayPatch, Pagination}
import com.ww.dtable.dao._
import com.ww.dtable.dao.table.HolidayDic
import com.ww.dtable.dao.table.HolidayDic._
import com.ww.dtable.domain.HolidayDicRow
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, LocalDate}
import slick.basic.DatabasePublisher
import slick.dbio.Effect
import slick.jdbc.MySQLProfile.api._
import slick.sql.FixedSqlAction
import com.ww.dtable.utils.DtableDays.currentDateTimeParse
import com.ww.dtable.slick.CustomMappers._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HolidayRepoImpl @Inject()(db: Database,
                                implicit val executionContext: ExecutionContext) extends HolidayRepo with StrictLogging {


  override def save(holidayPatchs: Seq[HolidayPatch]): Future[Unit] = {
    val holidayDicRows: Seq[HolidayDicRow] = holidayPatchs.map { patch =>
      val date = patch.date
      val now = DateTime.now()
      HolidayDicRow(hYear = date.getYear, hMonth = date.getMonthOfYear.toByte, holidayDesc = patch.holidayDesc, isHoliday = patch.dateType.code, hCurrentDate = patch.date, createTime = now, modifyTime = now)
    }
    val insert: FixedSqlAction[Option[Int], NoStream, Effect.Write] = holidayDicTable ++= holidayDicRows
    db.run(insert).map(_ => ())
  }

  override def save(currentDate: DateTime, isHoliday: DayType, holidayDesc: Option[String]): Future[Int] = {
    val now = DateTime.now()

    val insert: FixedSqlAction[Int, NoStream, Effect.Write] =
      (holidayDicTable += HolidayDicRow(hYear = currentDate.getYear.toByte, hMonth = currentDate.getMonthOfYear.toByte, hCurrentDate = currentDate, isHoliday = isHoliday.code, holidayDesc = holidayDesc, createTime = now, modifyTime = now))
    db.run(insert)
  }

  override def selectAll(): DatabasePublisher[HolidayDicRow] = {
    logger.info("select all holidays")

    db.stream {
      holidayDicTable.result
    }
  }

  override def query(queryFilter: Seq[QueryFilter] = Seq.empty, pagination: Option[Pagination] = None): DatabasePublisher[HolidayDicRow] = {
    val query = for {
      h <- holidayDicTable
      if (matchsFilter(queryFilter, h))
    } yield h

    val paginationQuery = pagination.map { p =>
      query.sortBy(_.hCurrentDate).drop(p.offset).take(p.limit)
    } getOrElse (query)
    db.stream {
      paginationQuery.result
    }
  }

  private def matchsFilter(filters: Seq[QueryFilter], hTable: HolidayDic.HolidayDicTable): Rep[Boolean] = {
    filters.map {
      case DayRangeFilter(ranges) =>
        val days = ranges.split(",").toSeq
        val dateRanges: Seq[DateTime] = days.map(currentDateTimeParse(_))
        hTable.hCurrentDate.inSet(dateRanges)

      case MonthFilter(months) => hTable.hMonth.inSet(months)
      case YearFilter(years) => hTable.hYear.inSet(years)
      case DayTypeFilter(dayType) => hTable.isHoliday === dayType.code
    }.reduceOption(_ && _).getOrElse(LiteralColumn(true))
  }


  override def getByDay(date: DateTime): Future[Option[HolidayDicRow]] = {
    val query = holidayDicTable.filter(_.hCurrentDate === date)
    query.result.statements.foreach(println)
    db.run(query.result).map(_.headOption)
  }

  override def updateHolidayStatus(holidays: DateTime, dayType: DayType): Future[Int] = {
    val query = holidayDicTable.filter(_.hCurrentDate === holidays)
    val updateResult = query.map(_.isHoliday).update(dayType.code)
    db.run(updateResult)
  }

  override def deleteByDays(holiday: Seq[DateTime]): Future[Int] = {
    val value = holidayQueryByDay(holiday)
    db.run(value.delete)
  }

  override def deleteByYear(year: Int): Future[Int] = {
    val query = holidayDicTable.filter(_.hYear === year)
    db.run(query.delete)
  }

  private def holidayQueryByDay(holiday: Seq[DateTime]) = {
    val value = for {
      h <- holidayDicTable
      if h.hCurrentDate.inSet(holiday)
    } yield h
    value
  }

}
