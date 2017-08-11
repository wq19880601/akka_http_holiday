package com.ww.dtable.api

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import org.joda.time.DateTime

import scala.annotation.meta.field

case class HolidayPatch(date: DateTime, dateType: DayType = DayType.Holiday, holidayDesc: Option[String])

@ApiModel
case class DayRange(@(ApiModelProperty@field) date: String, @(ApiModelProperty@field) desc: Option[String])

case class HolidayOut(date: DateTime, dayType: DayType, holidayDesc: Option[String], createTime: DateTime, modifyTime: DateTime)

@ApiModel
case class HolidayUpdate(@(ApiModelProperty@field) date: String, @(ApiModelProperty@field) dayType: DayType, @(ApiModelProperty@field) holidayDesc: Option[String])

@ApiModel
case class HolidayIn(@(ApiModelProperty@field) year: Int,
                     @(ApiModelProperty@field) workdays: Seq[DayRange],
                     @(ApiModelProperty@field) holidays: Seq[DayRange])

object HolidayIllegalTypeException extends RuntimeException("invalid holiday type")


abstract sealed class DayType {
  def code: Byte

  def strCode: String = code.toString
}

object DayType {
  def getDayType = (dayType: Byte) => dayType match {
    case 1 => DayType.Holiday
    case 0 => DayType.WorkDay
    case _ => throw HolidayIllegalTypeException
  }


  case object WorkDay extends DayType {
    override def code: Byte = 0

  }

  case object Holiday extends DayType {
    override def code: Byte = 1
  }

}
