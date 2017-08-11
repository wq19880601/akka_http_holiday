package com.ww.dtable.utils

import java.io.File

import com.google.inject.{AbstractModule, Provider}
import com.typesafe.config.{Config, ConfigFactory}
import com.ww.dtable.utils.ConfigModule.ConfigProvider
import net.codingwell.scalaguice.ScalaModule

object ConfigModule {
  class ConfigProvider extends Provider[Config] {
    override def get() = {
      val configFilePath = Option(System.getProperty("config.file"))

      val defaultConfig = ConfigFactory.load()

      configFilePath.map { filePath =>
        ConfigFactory
          .parseFile(new File(filePath))
          .withFallback(defaultConfig)
      }.getOrElse(defaultConfig)
    }
  }
}

/**
 * Binds the application configuration to the [[Config]] interface.
 *
 * The config is bound as an eager singleton so that errors in the config are detected
 * as early as possible.
 */
class ConfigModule extends AbstractModule with ScalaModule {

  override def configure() {
    bind[Config].toProvider[ConfigProvider].asEagerSingleton()
  }
}
