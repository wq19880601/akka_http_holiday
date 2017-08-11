package com.ww.dtable.routes

import javax.ws.rs.Path

import akka.NotUsed
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives.{complete, get, parameterMultiMap, _}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.google.inject.Inject
import com.typesafe.scalalogging.StrictLogging
import com.ww.dtable.RouteDirectives._
import com.ww.dtable.api.JsonProtocols._
import com.ww.dtable.api.{DayType, HolidayOut}
import com.ww.dtable.dao._
import com.ww.dtable.service.HolidayService
import io.swagger.annotations._
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * @author dpersa
  */

@Api(value = "/dtable", produces = "application/json")
@Path("/holidays")
class GetHolidays @Inject()(
                             executionContext: ExecutionContext,
                             holidayService: HolidayService,
                             implicit val actorMaterializer: ActorMaterializer) extends StrictLogging {

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()
    .withParallelMarshalling(parallelism = 8, unordered = false)


  @ApiOperation(value = "返回所有的节假日列表", notes = "所有年份的，数据量有点大", nickname = "getHolidays", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "return all holidays", response = classOf[HolidayOut]),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 404, message = "not found")
  ))
  def holidays = Unit

  @ApiOperation(value = "返回所有的节假日列表", notes = "所有年份的，数据量有点大", nickname = "getHolidays", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "day", required = false, dataType = "String", paramType = "query", value = "the day query, 2017-08-02 eg."),
    new ApiImplicitParam(name = "day_type", required = false, dataType = "integer", paramType = "query", value = "the day type query, 0 or 1"),
    new ApiImplicitParam(name = "year", required = false, dataType = "integer", paramType = "query", value = "the year query, 2017"),
    new ApiImplicitParam(name = "month", required = false, dataType = "integer", paramType = "query", value = "the month query, 12")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "return all holidays", response = classOf[HolidayOut]),
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 404, message = "not found")
  ))
  @Path("/search")
  def search = Unit


  val route =
    pathPrefix("holidays") {
      pathEndOrSingleSlash {
        get {
          val holidays: Source[HolidayOut, NotUsed] = holidayService.queryAll()
          holidays.runForeach { h =>
            logger.info("daytype={}", h.toJson)
          }
          complete(holidays)
        }
      } ~ path("search") {
        get {
          parameterMultiMap { parameterMultiMap =>
            extractPagination(parameterMultiMap) { paganation =>
              val filters: List[QueryFilter] = extractFilters(parameterMultiMap)
              val holidays: Source[HolidayOut, NotUsed] = holidayService.queryByFilter(filters, paganation)
              complete(holidays)
            }
          }
        }
      }
    }

  private def extractFilters(parameterMultiMap: Map[String, Seq[String]]): List[QueryFilter] = {
    parameterMultiMap.flatMap {
      case ("day", dayRanges) =>
        dayRanges.headOption.map(DayRangeFilter)

      case ("day_type", dayTypes) =>
        val dayTypeBytes = dayTypes.headOption.flatMap { str =>
          Try(str.toByte).flatMap { x =>
            Try(DayType.getDayType(x))
          }.toOption
        }
        dayTypeBytes.map(DayTypeFilter)

      case ("year", years) =>
        val yearInts = years.flatMap { yearString =>
          Try(yearString.toInt).toOption
        }
        Some(yearInts).filter(_.nonEmpty).map(YearFilter)

      case ("month", months) =>
        val bytes = months.flatMap { monthString =>
          Try(monthString.toByte).toOption
        }
        Some(bytes).filter(_.nonEmpty).map(MonthFilter)

      case _ => None
    }
  }.toList
}
