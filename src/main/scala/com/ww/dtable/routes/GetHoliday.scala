package com.ww.dtable.routes

import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives._
import com.google.inject.Inject
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.api.HolidayOut
import com.ww.dtable.service.HolidayService
import com.ww.dtable.utils.DtableDays
import com.ww.dtable.api.JsonProtocols._
import io.swagger.annotations._

import scala.concurrent.ExecutionContext

/**
  * @author dpersa
  */
@Api(value = "/dtable", produces = "application/json")
@Path("/holiday")
class GetHoliday @Inject()(
                            executionContext: ExecutionContext,
                            holidayService: HolidayService) extends StrictLogging {

  @ApiOperation(value = "查询单个节假日信息", notes = "根据日期查询对应的信息", nickname = "get_holiday", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "date", required = true, dataType = "String", paramType = "query", value = "the day query, 2017-08-02 eg.")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "get holiday by date", response = classOf[HolidayOut]),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 404, message = "not found")
  ))
  def holiday = Unit

  val route =
    (path("holiday")  & parameter('date)){ dateStr =>
      get {
        val date = DtableDays.currentDateTimeParse(dateStr)
        val getByDateResult = holidayService.getByDay(date)
        onSuccess(getByDateResult) { holiday =>
          complete(holiday)
        }
      }
    }
}

