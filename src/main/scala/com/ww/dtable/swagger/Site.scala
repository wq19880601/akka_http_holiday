package com.ww.dtable.swagger

import akka.http.scaladsl.server.Directives._
trait Site extends  WebJarsSupport{
  val site =
    path("swagger") { getFromResource("swagger/index.html") } ~
      getFromResourceDirectory("swagger")
}