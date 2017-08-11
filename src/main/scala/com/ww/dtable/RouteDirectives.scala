package com.ww.dtable

import akka.http.scaladsl.server._
import com.ww.dtable.api.Pagination

import scala.util.Try

trait RouteDirectives {

  def extractPagination(parameterMultiMap: Map[String, Seq[String]]): Directive1[Option[Pagination]] =
    Directive[Tuple1[Option[Pagination]]] { inner =>
      ctx => {
        val limit = parameterMultiMap.get("limit")
          .flatMap(_.headOption)
          .flatMap(s => Try(s.toInt).toOption)

        def offset = parameterMultiMap.get("offset")
          .flatMap(_.headOption)
          .flatMap(s => Try(s.toInt).toOption)
          .getOrElse(0)

        val pagination = limit.map(Pagination(offset, _))

        inner(Tuple1(pagination))(ctx)
      }
    }

}

object RouteDirectives extends RouteDirectives