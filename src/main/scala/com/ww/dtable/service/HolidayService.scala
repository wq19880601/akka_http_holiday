package com.ww.dtable.service


import java.sql.Date

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.google.inject.Inject
import com.ww.dtable.api._
import com.ww.dtable.dao.{HolidayRepo, QueryFilter}
import com.ww.dtable.domain.HolidayDicRow
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, Period}
import slick.basic.DatabasePublisher
import slick.lifted.{Rep, SimpleFunction, SimpleLiteral}

import scala.concurrent.{ExecutionContext, Future}

trait HolidayService {

  def calculateHolidays(year: Int, holidays: Seq[DayRange], workDays: Seq[DayRange]): Future[Unit]

  def queryAll(): Source[HolidayOut, NotUsed]

  def queryByFilter(filters: Seq[QueryFilter], pagination: Option[Pagination]): Source[HolidayOut, NotUsed]

  def getByDay(date: DateTime): Future[Option[HolidayOut]]

  def deleteByYear(year: Int): Future[Int]

  def deleteByDay(days: Seq[DateTime]): Future[Int]

  def toggleTheDay(day: DateTime, dayType: DayType): Future[Int]
}

object HolidayService {


  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val weekendDescStr = "双休日"
  val workDayStr = "正常工作日"
  val END_CHAR = '.'
  val DASH_CHAR = '-'

  val isWeekend = (num: Int) => num == 7 || num == 6


  def date2Str = (specifyDay: DateTime) => specifyDay.toString(formatter)

  def str2Date = (specifyDay: String) => DateTime.parse(specifyDay, formatter)

  def dateRange(from: DateTime, to: DateTime, step: Period): Seq[DateTime]
  = Iterator.iterate(from)(_.plus(step)).takeWhile(!_.isAfter(to)).toSeq


  def getDayType = (holidayType: Byte) => holidayType match {
    case 1 => DayType.Holiday
    case 0 => DayType.WorkDay
    case _ => throw HolidayIllegalTypeException
  }

  def sizeRequire = (size: Int) => require(size == 2, "invalid size")
}

class HolidayServiceImpl @Inject()(val holidayRepo: HolidayRepo, implicit val executionContext: ExecutionContext) extends HolidayService {

  import HolidayService._


  override def toggleTheDay(day: DateTime, dayType: DayType): Future[Int] = {
    holidayRepo.updateHolidayStatus(day, dayType)
  }


  override def queryByFilter(filters: Seq[QueryFilter], pagination: Option[Pagination]): Source[HolidayOut, NotUsed] = {
    holidayRowsStreamToHolidayOutStream(holidayRepo.query(filters, pagination))
  }

  override def deleteByYear(year: Int): Future[Int] = {
    holidayRepo.deleteByYear(year)
  }

  override def deleteByDay(days: Seq[DateTime]): Future[Int] = {
    holidayRepo.deleteByDays(days)
  }


  private def holidayRowsStreamToHolidayOutStream(streamHolidayRows: => DatabasePublisher[HolidayDicRow]): Source[HolidayOut, NotUsed] = {
    Source.fromPublisher(
      streamHolidayRows.mapResult {
        holidayRow =>
          val dayType = getDayType(holidayRow.isHoliday)
          HolidayOut(holidayRow.hCurrentDate, dayType, holidayRow.holidayDesc, holidayRow.createTime, holidayRow.modifyTime)
      }
    )
  }

  override def queryAll(): Source[HolidayOut, NotUsed] = {
    //    val function: (Rep[Date]) => Rep[Int] = SimpleFunction.unary[Date,Int]("day_of_week")
    //    def dayOfWeek2(c:Rep[Date]) = SimpleFunction[Int]("day_of_week").apply(Seq(c))
    //    val currentdate: Rep[Date] = SimpleLiteral[Date]("CURRENT_DATE")


    holidayRowsStreamToHolidayOutStream(holidayRepo.selectAll())
  }


  override def getByDay(date: DateTime): Future[Option[HolidayOut]] = {
    holidayRepo.getByDay(date).map {
      case Some(holidayDicRow) => {
        val holidayOut = HolidayOut(holidayDicRow.hCurrentDate, getDayType(holidayDicRow.isHoliday), holidayDicRow.holidayDesc, holidayDicRow.createTime, holidayDicRow.modifyTime)
        Some(holidayOut)
      }
      case _ => None
    }
  }

  private def parseFromDateStr(year: Int, dayRange: DayRange)(dayType: DayType): Seq[HolidayPatch] = {
    assert(dayRange.date.nonEmpty, s"invalid date string,${dayRange.date}")

    if (dayRange.date.indexOf(DASH_CHAR) > -1) {
      val startEndDays = dayRange.date.split(DASH_CHAR)
      val startDay: String = startEndDays(0)
      val startDayArr: Array[String] = startDay.split(END_CHAR)
      sizeRequire(startDayArr.size)

      val startDate = new DateTime(year, startDayArr(0).toInt, startDayArr(1).toInt, 0, 0, 0)

      val endDays: String = startEndDays(1)
      val endDayArr: Array[String] = endDays.split(END_CHAR)
      sizeRequire(startDayArr.size)

      val endDate = new DateTime(year, endDayArr(0).toInt, endDayArr(1).toInt, 0, 0, 0)
      val days = dateRange(startDate, endDate, Period.days(1))
      val holidayPatches: Seq[HolidayPatch] = days.map { h =>
        HolidayPatch(h, DayType.Holiday, dayRange.desc)
      }
      holidayPatches
    } else {
      val day = singleDay(year, dayRange, dayType)
      Seq(day)
    }
  }


  private def singleDay(year: Int, dayRange: DayRange, dayType: DayType) = {
    val days: Array[String] = dayRange.date.split(END_CHAR)
    sizeRequire(days.size)

    val specifyDay = new DateTime(year, days(0).toInt, days(1).toInt, 0, 0, 0)
    val holidayPatch = HolidayPatch(specifyDay, dayType, dayRange.desc)
    holidayPatch
  }

  override def calculateHolidays(year: Int, holidays: Seq[DayRange] = Seq.empty, workDays: Seq[DayRange] = Seq.empty): Future[Unit] = {
    if (year < 1970) {
      return Future.failed(new RuntimeException(s"invalida year,$year"))
    }
    holidayRepo.deleteByYear(year).map { _ =>
      val trueHolidays: Seq[HolidayPatch] = getDestHolidays(year, holidays, workDays)
      holidayRepo.save(trueHolidays)
    }
  }

  private def getDestHolidays(year: Int, holidays: Seq[DayRange], workDays: Seq[DayRange]) = {
    val yearStart = new DateTime(year, 1, 1, 0, 0, 0)
    val yearEnd = yearStart.plusYears(1).minusDays(1)
    val yearDays = dateRange(yearStart, yearEnd, Period.days(1))

    // 双休日
    val weekendDics: Seq[HolidayPatch] = yearDays.filter(x => isWeekend(x.getDayOfWeek)).map(x => {
      HolidayPatch(x, DayType.Holiday, Some(weekendDescStr))
    })

    // 节假日
    val holidayDics: Seq[HolidayPatch] = holidays.flatMap { h => parseFromDateStr(year, h)(DayType.Holiday) }
    // 节前后需要补班
    val workDayDics: Seq[HolidayPatch] = workDays.flatMap { w => parseFromDateStr(year, w)(DayType.WorkDay) }


    val dayPatches: Seq[HolidayPatch] = yearDays.map { yd =>
//      val dateStr = date2Str(yd)

      holidayDics.find(x => x.date == yd) match {
        case Some(h) => HolidayPatch(yd, DayType.Holiday, h.holidayDesc) // holiday
        case None =>
          workDayDics.find(xx => xx.date == yd) match {
            case Some(h) => HolidayPatch(yd, DayType.WorkDay, h.holidayDesc) // workday
            case None =>
              if (isWeekend(yd.getDayOfWeek)) {
                HolidayPatch(yd, DayType.Holiday, Some(weekendDescStr)) // weekend
              } else {
                HolidayPatch(yd, DayType.WorkDay, Some(workDayStr)) // workday
              }
          }
      }
    }
    dayPatches
  }
}


