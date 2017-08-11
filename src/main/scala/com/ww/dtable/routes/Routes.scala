package com.ww.dtable.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, RouteResult}
import com.google.inject.{Inject, Singleton}
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.DtableRejectionHandler
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.ww.dtable.swagger.{SwaggerDoc, WebJarsSupport}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author dpersa
  */
@Singleton
class Routes @Inject()(
                        getHoliday: GetHoliday,
                        deleteHoliday: DeleteHoliday,
                        patchHoliday: PatchHoliday,
                        getHolidays: GetHolidays,
                        generateHolidays: GenerateHolidays,
                        rejectionHandler: DtableRejectionHandler,
                        implicit val executionContext: ExecutionContext) extends StrictLogging {

  val route: RequestContext => Future[RouteResult] =
    extractRequestContext { requestContext =>
      val requestStartTime = System.currentTimeMillis()
      val requestMethod = requestContext.request.method.value
      val requestPath = requestContext.request.uri.path.toString()
      extractClientIP { remoteAddress =>
        mapResponse(response => {
          val requestDuration = System.currentTimeMillis() - requestStartTime
          val statusCode = response.status.intValue()
          logger.info(
            "{} {} {} {} {} {}",
            remoteAddress.toIP.map(_.toString()).getOrElse("no-remote-address"),
            requestMethod,
            requestContext.request.uri.path.toString() + requestContext.request.uri.rawQueryString.map('?' + _).getOrElse(""),
            requestContext.request.protocol.value,
            statusCode.toString,
            requestDuration.toString
          )
          response
        }) {
          handleRejections(rejectionHandler()) {
            val reqDesc = s"$requestMethod $requestPath"
            val routes = getHolidays.route ~ deleteHoliday.route ~ getHoliday.route ~ patchHoliday.route ~ generateHolidays.route
            cors()(routes ~ SwaggerDoc.routes) ~ WebJarsSupport.webJars
          } ~ path("status") {
            complete("Ok")
          }
        }
      }
    }

}
