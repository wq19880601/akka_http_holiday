package com.ww.dtable.api


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import spray.json.{JsString, JsValue, RootJsonFormat}

object DateTimeProtocol extends SprayJsonSupport {

  implicit object LocalDateTimeFormat extends RootJsonFormat[DateTime] {

    private val formatter = "yyyy-MM-dd HH:mm:ss"

    override def read(json: JsValue): DateTime = json match {

      case JsString(s) => DateTime.parse(s, DateTimeFormat.forPattern(formatter))
      case _ => throw new IllegalArgumentException("expected string")
    }

    override def write(obj: DateTime): JsValue = JsString(obj.toString(formatter))
  }

}
