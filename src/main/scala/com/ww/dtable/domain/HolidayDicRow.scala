package com.ww.dtable.domain

import org.joda.time.DateTime

/** Entity class storing rows of table holidayDicTable
  *  @param id
  *  @param hYear
  *  @param hMonth
  *  @param hCurrentDate
  *  @param isHoliday
  *  @param holidayDesc
  *  @param createTime
  *  @param modifyTime  */
case class HolidayDicRow(id: Option[Int] = None, hYear: Int, hMonth: Byte, hCurrentDate: DateTime, isHoliday: Byte, holidayDesc: Option[String] = None, createTime: DateTime, modifyTime: DateTime)
