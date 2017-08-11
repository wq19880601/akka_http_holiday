package com.ww.dtable.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{JsString, JsValue, RootJsonFormat, _}
import com.ww.dtable.api.DateTimeProtocol.LocalDateTimeFormat
import org.joda.time.DateTime

object JsonProtocols extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val errorFormat = jsonFormat(Error, "status", "title", "type", "detail")

  implicit object DayTypeFormat extends RootJsonFormat[DayType] {
    val workDayCodeStr = DayType.WorkDay.strCode
    val holiyDayCodeStr = DayType.Holiday.strCode

    override def read(json: JsValue): DayType = json match {
      case JsString(workDayCodeStr) => DayType.WorkDay
      case JsString(holiyDayCodeStr) => DayType.Holiday
      case _ => deserializationError("invalid type")
    }

    override def write(obj: DayType): JsValue = JsString(obj.code.toString)
  }

  implicit val dayRangeFormat = jsonFormat(DayRange, "date", "desc")

  implicit val holidayInForamt = jsonFormat(HolidayIn, "year", "workdays", "holidays")


  implicit  val holidayPatchFormat = jsonFormat(HolidayUpdate,"date","dayType","holidayDesc")
  implicit val holidayOutForamt = jsonFormat(HolidayOut, "date", "day_type", "holiday_desc", "create_time", "modify_time")


    def main(args:Array[String]) = {
//      val ranges: Seq[DayRange] = Seq(DayRange("1.1-1.3", Some("aaaa")))
//      val in = HolidayIn(2017, Seq(DayRange("1.1-1.3", Some("aaaa"))), ranges)

      val holidayout = HolidayOut(DateTime.now(),DayType.WorkDay,Some("hello"),DateTime.now(),DateTime.now())
      println(holidayout.toJson)

    }

}
