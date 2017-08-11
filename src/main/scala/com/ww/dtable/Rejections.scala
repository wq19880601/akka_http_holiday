package com.ww.dtable

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Rejection

sealed trait DtableRejection {
  def statusCode: StatusCode

  def message: String

  def code: String

  def requestDescription: String
}

object Rejections {


  case class RouteNotFoundRejection(requestDescription: String) extends Rejection with DtableRejection {
    override def statusCode: StatusCode = StatusCodes.NotFound

    override def message: String = requestDescription

    override def code: String = "RNF"
  }

  case class InternalServerErrorRejection(requestDescription: String) extends Rejection with DtableRejection {
    override def statusCode: StatusCode = StatusCodes.InternalServerError

    override def message: String = requestDescription

    override def code: String = "ISE"
  }

  case class InvalidRouteNameRejection(requestDescription: String, msg: String) extends Rejection with DtableRejection {
    override def statusCode: StatusCode = StatusCodes.BadRequest

    override def message: String = s"invalid route format $msg"

    override def code: String = "IRP"
  }

}
