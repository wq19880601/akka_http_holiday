package com.ww.dtable.slick

import java.sql.Date

import org.joda.time.DateTime
import slick.jdbc.MySQLProfile.api._

object CustomMappers {

  implicit def dateTimeMapper = MappedColumnType.base[DateTime, Date](
    { dateTime => new Date(dateTime.getMillis) }, { date => new DateTime(date.getTime) }
  )

}
