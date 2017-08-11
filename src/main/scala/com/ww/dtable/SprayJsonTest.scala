package com.ww.dtable

import spray.json._


object SprayJsonTest {

  case class Person(name: String, age: Int, address: Option[String] = None)

  class Colordd(val name: String, val red: Int, val blue: Int, val yellow: Int) {
    override def toString: String = s"name=$name,red=$red, blue=$blue,yellow=$yellow"
  }


  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val personJsonFormat = jsonFormat(Person, "name_", "age_", "address_")

    /*
        implicit object ColorJsonFormat extends RootJsonFormat[Colordd] {
          override def write(obj: Colordd): JsValue = {
            JsArray(JsString(obj.name), JsNumber(obj.red), JsNumber(obj.blue), JsNumber(obj.yellow))
          }

          override def read(json: JsValue): Colordd = json match {
            case JsArray(Vector(JsString(name), JsNumber(red), JsNumber(blue), JsNumber(yellow))) =>
              new Colordd(name, red.toInt, blue.toInt, yellow.toInt)
            case _ => deserializationError("color not found")
          }

        }
    */

    implicit object ColorJsonForamt extends RootJsonFormat[Colordd] {
      override def write(obj: Colordd): JsValue =
        JsObject(
          "name_" -> JsString(obj.name),
          "red_" -> JsNumber(obj.red),
          "blue_" -> JsNumber(obj.blue),
          "yellow_" -> JsNumber(obj.yellow)
        )


      override def read(json: JsValue): Colordd = {
        val fields = json.asJsObject.fields

        json.asJsObject.getFields("name_", "red_", "blue_", "yellow_") match {
          case Seq(JsString(name), JsNumber(red), JsNumber(blue), JsNumber(yellow)) => new Colordd(name, red.toInt, blue.toInt, yellow.toInt)
          case _ => deserializationError("color not found")
        }
      }
    }

  }


  def main(args: Array[String]): Unit = {
    import MyJsonProtocol._


    val listJson = List(1, 2, 3).toJson
    val ints = listJson.convertTo[List[Int]]
    println(s"listJson,source, $listJson, $ints")

    //    val optionJSon = Some("hello").toJson
    //    val optionJSonNone = None.toJson
    //    println(s"optionJson, noneJson,$optionJSon, $optionJSonNone")

    def personJson = {
      val person = Person("jack", 20)
      val personJson = person.toJson
      println(s"personJson, ${personJson}")


      val personJsonStr = """{"name_":"jack","age_":20}"""
      val personParse = personJsonStr.parseJson
      val originPersonObj = personParse.convertTo[Person]
      println(s"person parse, $originPersonObj")
    }

    //    personJson

    def tupleJson = {
      val tupleobj = (1, 2)
      val tupleJson = tupleobj.toJson
      println(s"tupleJson, $tupleJson")
    }

    //    tupleJson


    //    val rightObj =Right("200")
    //    val rightJson = rightObj.toJson
    //    println(s"rightJson,$rightJson")

    val colorJson = new Colordd("red", 120, 244, 120).toJson
    println(s"colorJson, $colorJson")
    val color = colorJson.convertTo[Colordd]
    println(s"colorobj, $color")
  }

}
