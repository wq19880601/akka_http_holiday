package com.ww.dtable.utils

import com.google.inject.{AbstractModule, Inject, Provider, Singleton}
import com.ww.dtable.api.AkkaModule.ExecutionContextProvider
import com.ww.dtable.dao.HolidayRepo
import com.ww.dtable.dao.impl.HolidayRepoImpl
import net.codingwell.scalaguice.ScalaModule
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

/**
  * @author dpersa
  */
@Singleton
class DbProvider @Inject()(config: EnvConfig) extends Provider[Database] {
  override def get() = Database.forConfig(s"${config.env}.dtabledb")
}

class DbExecutionContextProvider extends Provider[ExecutionContext] {
  override def get() = ExecutionContext.global
}

class DbModule extends AbstractModule with ScalaModule {

  override def configure() {
    bind[Database].toProvider[DbProvider].asEagerSingleton()
    bind[ExecutionContext].toProvider[ExecutionContextProvider].asEagerSingleton()
    bind[HolidayRepo].to[HolidayRepoImpl].asEagerSingleton()
  }
}