package com.ww.dtable.routes

import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives._
import com.google.inject.Inject
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.Rejections.InternalServerErrorRejection
import com.ww.dtable.api.{HolidayIn, HolidayOut}
import com.ww.dtable.service.HolidayService
import com.ww.dtable.api.JsonProtocols._
import io.swagger.annotations._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * @author dpersa
  */
@Api(value = "/dtable", produces = "application/json")
@Path("/holidays/calculate")
class GenerateHolidays @Inject()(
                                  executionContext: ExecutionContext,
                                  holidayService: HolidayService) extends StrictLogging {

  @ApiOperation(value = "查询单个节假日信息", notes = "根据日期查询对应的信息", nickname = "get_holiday", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "date", required = true, dataTypeClass = classOf[HolidayIn], paramType="body", value = "the day query, 2017-08-02 eg.")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "generate holiday by the year and the holiday infos"),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 404, message = "not found")
  ))
  def genHolidays = Unit

  val route =
    path("holidays" / "calculate") {
      post {
        entity(as[HolidayIn]) { holidayIn =>
          val calculateHolidays = holidayService.calculateHolidays(holidayIn.year, holidayIn.holidays, holidayIn.workdays)
          onComplete(calculateHolidays) {
            case Success(_) => complete("calucate success")
            case Failure(ex) =>
              logger.error("calcualte holiday occur error", ex)
              reject(InternalServerErrorRejection(ex.getMessage))
          }
        }
      }
    }
}
