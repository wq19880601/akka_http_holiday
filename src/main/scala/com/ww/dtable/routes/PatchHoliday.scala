package com.ww.dtable.routes

import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.api.{HolidayPatch, HolidayUpdate}
import com.ww.dtable.api.JsonProtocols._
import com.ww.dtable.service.HolidayService
import com.ww.dtable.utils.DtableDays._
import io.swagger.annotations._

import scala.concurrent.ExecutionContext

/**
  * @author dpersa
  */
@Api(value = "/dtable", produces = "application/json")
@Path("/holiday/update")
class PatchHoliday @Inject()(
                              executionContext: ExecutionContext,
                              holidayService: HolidayService) extends StrictLogging {

  @ApiOperation(value = "更新对应日期为工作日或者非工作日", notes = "根据所选日期进行更新", nickname = "update to workday or holiday", httpMethod = "Post")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "holidayPatch", required = true, dataTypeClass = classOf[HolidayUpdate], paramType = "body", value = "the days to update, 2017-08-02,2017-08-03")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "generate holiday by the year and the holiday infos"),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 404, message = "not found")
  ))
  def updateHoliday = Unit

  val route: Route =
    path("holiday" / "update") {
      post {
        entity(as[HolidayUpdate]) { patch =>
          val date = currentDateTimeParse(patch.date)
          val udpateResult = holidayService.toggleTheDay(date, patch.dayType)
          onSuccess(udpateResult) { number =>
            complete(s"path success, $number")
          }
        }
      }
    }
}
