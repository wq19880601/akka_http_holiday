package com.ww.dtable

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{MethodRejection, RejectionHandler}
import com.google.inject.{Inject, Singleton}
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.api.Error
import com.ww.dtable.api.JsonProtocols.errorFormat
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._


/**
 * @author dpersa
 */
@Singleton
class DtableRejectionHandler @Inject()() extends StrictLogging {

  def apply(): RejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case rejection: DtableRejection =>
          extractRequest { req =>
            logger.error(s"The request ${rejection.requestDescription} was rejected: $rejection entity: ${req.entity.toString}")
            complete(
              rejection.statusCode,
              Error(rejection.statusCode.intValue, rejection.message, rejection.code)
            )
          }
      }.handleAll[MethodRejection] { methodRejections =>
        val names = methodRejections.map(_.supported.name)
        val message = s"Method not allowed! Supported: ${names mkString " or "}!"
        logger.error(message)
        complete(
          StatusCodes.MethodNotAllowed,
          Error(
            StatusCodes.MethodNotAllowed.intValue,
            message,
            "MNA")
        )
      }.handleNotFound {
        complete(
          StatusCodes.NotFound,
          Error(StatusCodes.NotFound.intValue, "Resource not found", "RENF")
        )
      }.result()
}
