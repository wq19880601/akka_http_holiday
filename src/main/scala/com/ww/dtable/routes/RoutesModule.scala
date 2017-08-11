package com.ww.dtable.routes

import com.google.inject.AbstractModule
import com.ww.dtable.DtableRejectionHandler
import com.ww.dtable.swagger.SwaggerDoc
import net.codingwell.scalaguice.ScalaModule

/**
 * @author dpersa
 */
class RoutesModule extends AbstractModule with ScalaModule {

  override def configure() {
    bind[Routes].asEagerSingleton()

    // routes
    bind[GetHoliday].asEagerSingleton()
    bind[DeleteHoliday].asEagerSingleton()
    bind[GetHolidays].asEagerSingleton()
    bind[PatchHoliday].asEagerSingleton()
    bind[GenerateHolidays].asEagerSingleton()



    // other
    bind[DtableRejectionHandler].asEagerSingleton()
  }
}
