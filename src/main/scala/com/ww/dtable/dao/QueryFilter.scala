package com.ww.dtable.dao

import com.ww.dtable.api.DayType

sealed trait QueryFilter

case class DayRangeFilter(days: String) extends QueryFilter

case class DayTypeFilter(dayType: DayType) extends QueryFilter

case class YearFilter(year: Seq[Int]) extends QueryFilter

case class MonthFilter(month: Seq[Byte]) extends QueryFilter
