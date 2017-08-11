package com.ww.dtable.dao

import com.ww.dtable.api.{DayType, HolidayPatch, Pagination}
import com.ww.dtable.domain.HolidayDicRow
import org.joda.time.DateTime
import slick.basic.DatabasePublisher

import scala.concurrent.Future

trait HolidayRepo {

  def save(holidayPatchs: Seq[HolidayPatch]):Future[Unit]

  def save(currentDate: DateTime, isHoliday: DayType, holidayDesc: Option[String]): Future[Int]

  def selectAll(): DatabasePublisher[HolidayDicRow]

  def query(queryFilter: Seq[QueryFilter], pagination: Option[Pagination]):DatabasePublisher[HolidayDicRow]


  def updateHolidayStatus(holidays: DateTime, dayType: DayType): Future[Int]

  def deleteByDays(holiday:Seq[DateTime]):Future[Int]

  def deleteByYear(year:Int):Future[Int]

  def getByDay(date:DateTime):Future[Option[HolidayDicRow]]
}

