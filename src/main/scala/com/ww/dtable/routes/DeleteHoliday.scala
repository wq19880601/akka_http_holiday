package com.ww.dtable.routes

import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives._
import com.google.inject.Inject
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.api.HolidayIn
import com.ww.dtable.service.HolidayService
import io.swagger.annotations._

import scala.concurrent.ExecutionContext

/**
  * @author dpersa
  */
@Api(value = "/dtable", produces = "application/json")
@Path("/holiday/{year}")
class DeleteHoliday @Inject()(
                               executionContext: ExecutionContext,
                               holidayService: HolidayService) extends StrictLogging {

  @ApiOperation(value = "删除信息", notes = "根据年删除数据", nickname = "get_holiday", httpMethod = "DELETE")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "year", required = true, dataType = "integer", paramType = "path", value = "the year delete, 2017 eg.")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "delete the holiday by the year"),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 404, message = "not found")
  ))
  def holidayDelete = Unit

  val route = {
    path("holiday" / IntNumber) { year =>
      delete {
        val deleteResult = holidayService.deleteByYear(year)
        onSuccess(deleteResult) { _ =>
          complete("delete success")
        }
      }
    }
  }

}
