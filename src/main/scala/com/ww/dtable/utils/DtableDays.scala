package com.ww.dtable.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object DtableDays {

  def currentDateTimeParse = (currentDate: String) => DateTime.parse(currentDate, DateTimeFormat.forPattern("yyyy-MM-dd"))

}
