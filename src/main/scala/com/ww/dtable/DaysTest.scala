package com.ww.dtable

import java.time.LocalDate

import org.joda.time.{DateTime, Period}

object DaysTest extends  App{

  def dateRange(from: DateTime, to: DateTime, step: Period): Iterator[DateTime]
  =Iterator.iterate(from)(_.plus(step)).takeWhile(!_.isAfter(to))

  val yearStart = new DateTime(2017,1,1,0,0,0)
  val yearEnd = yearStart.plusYears(1).minusDays(1)

  dateRange(yearStart,yearEnd,Period.days(1)).foreach{d => println(d.toString())}




}
