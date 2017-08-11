package com.ww.dtable.api

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.google.inject.{AbstractModule, Injector, Provider}
import com.typesafe.config.Config
import com.ww.dtable.api.AkkaModule.{ActorSystemProvider, ExecutionContextProvider, LoggingProvider}
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContextExecutor

object AkkaModule {

  class ActorSystemProvider @Inject() (val config: Config, val injector: Injector)
      extends Provider[ActorSystem] {

    override def get() = {
      val system = ActorSystem("main-actor-system", config)
      system
    }
  }

  class LoggingProvider @Inject() (implicit val actorSystem: ActorSystem)
      extends Provider[LoggingAdapter] {

    override def get(): LoggingAdapter = Logging(actorSystem, getClass)
  }

  class ExecutionContextProvider @Inject() (implicit val actorSystem: ActorSystem)
      extends Provider[ExecutionContextExecutor] {

    override def get(): ExecutionContextExecutor = actorSystem.dispatcher
  }
}

/**
 * A module providing an Akka ActorSystem.
 */
class AkkaModule extends AbstractModule with ScalaModule {

  override def configure() {
    bind[ActorSystem].toProvider[ActorSystemProvider].asEagerSingleton()
    bind[LoggingAdapter].toProvider[LoggingProvider].asEagerSingleton()
    bind[ExecutionContextExecutor].toProvider[ExecutionContextProvider].asEagerSingleton()
  }
}
