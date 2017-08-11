package com.ww.dtable

import com.google.inject.{Guice, Injector}
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.api.{AkkaHttp, AkkaHttpModule, AkkaModule}
import com.ww.dtable.routes.RoutesModule
import com.ww.dtable.service.ServicesModule
import com.ww.dtable.utils.{ConfigModule, DbModule, EnvConfig, UtilsModule}
import net.codingwell.scalaguice.InjectorExtensions._

/**
  * @author dpersa
  */
object Dtable extends App with StrictLogging {


  private val injector: Injector = Guice.createInjector(
    new ConfigModule(),
    new DbModule(),
    new UtilsModule(),
    new ServicesModule(),
    new AkkaModule(),
    new RoutesModule(),
    new AkkaHttpModule()
  )

  private val config = injector.instance[EnvConfig]

  private val env = config.env

  logger.info(s"dtable.env=$env")

  val akkaHttp = injector.instance[AkkaHttp]

  akkaHttp.run()

  logger.info(s"Listening on port ${config.getInt("port")}...")

}
