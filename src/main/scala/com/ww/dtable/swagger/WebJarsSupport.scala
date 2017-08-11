package com.ww.dtable.swagger

import org.webjars.WebJarAssetLocator
import akka.http.scaladsl.server.Directives._

import scala.util.{Failure, Success, Try}

trait WebJarsSupport {
  val webJarAssetLocator = new WebJarAssetLocator

  final def webJars = {
    pathPrefix("swagger-ui") {
      extractUnmatchedPath { path =>
        Try(webJarAssetLocator.getFullPath("swagger-ui", path.toString())) match {
          case Success(fullPath) =>
            getFromResource(fullPath)
          case Failure(_: IllegalArgumentException) =>
            reject
          case Failure(e) =>
            failWith(e)
        }
      }
//      val swaggerPath = "META-INF/resources/webjars/swagger-ui/3.1.4"
//      pathEndOrSingleSlash {
//        getFromResourceDirectory(swaggerPath)
//      } ~
//        path("index.html") {
//          getFromResource(swaggerPath + "/index.html")
//        }
    }
  }
}

object WebJarsSupport extends WebJarsSupport