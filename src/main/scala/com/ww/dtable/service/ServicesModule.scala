package com.ww.dtable.service

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

/**
  * @author dpersa
  */
class ServicesModule extends AbstractModule with ScalaModule {

  override def configure() {
    bind[HolidayService].to[HolidayServiceImpl].asEagerSingleton()
  }
}
