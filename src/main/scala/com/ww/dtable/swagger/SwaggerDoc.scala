package com.ww.dtable.swagger

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import com.ww.dtable.routes._
import io.swagger.models.auth.BasicAuthDefinition

object SwaggerDoc extends  SwaggerHttpService{
  override def apiClasses: Set[Class[_]] = Set(classOf[GetHolidays],classOf[GetHoliday],classOf[GenerateHolidays], classOf[DeleteHoliday],classOf[PatchHoliday])

  override val host = "localhost:9080"
  override val info = Info(version = "1.0")
  override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
}
