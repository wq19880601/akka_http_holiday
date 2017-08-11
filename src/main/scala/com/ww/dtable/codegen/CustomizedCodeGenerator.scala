package com.ww.dtable.codegen

import com.ww.dtable.codegen.Config._
import slick.codegen.SourceCodeGenerator
import slick.jdbc.MySQLProfile
import slick.model.Model

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

class CustomizedCodeGenerator(model: Model) extends SourceCodeGenerator(model) {


  override def code: String = "import com.github.tototoshi.slick.MySQLJodaSupport._\n" + "import org.joda.time.DateTime\n" + super.code

  // customize Scala entity name (case class, etc.)
  override def entityName = dbTableName => dbTableName match {
    case "COFFEES" => "Coffee"
    case "SUPPLIERS" => "Supplier"
    case "Holiday" => "CoffeeInventoryItem"
    case _ => super.entityName(dbTableName)
  }



  // customize Scala table name (table class, table values, ...)
  override def tableName = dbTableName => dbTableName match {
    case "holiday_dic" => "HolidayDicTable"
    case _ => super.tableName(dbTableName)
  }


  // override generator responsible for tables
  override def Table = new Table(_) {
    table =>
    // customize table value (TableQuery) name (uses tableName as a basis)
    override def TableValue = new TableValue {
      override def rawName = super.rawName.uncapitalize

      override def doc: String = ""
    }


    // override generator responsible for columns
    override def Column = new Column(_) {

      override def rawType: String = model.tpe match {
        case "java.sql.Timestamp" => "DateTime"
        case _ =>
          print(s"${model.table.table}#${model.name}#tpe=${model.tpe} rawType=${super.rawType}")
          super.rawType
      }

      override def doc: String = ""

      override def asOption: Boolean = this.model.name match {
        case "id" => true
//        case "create_time" => true
//        case "modify_time" => true
        case "app_id" => true
        case _ => false
      }

      // customize Scala column names
      override def rawName = (table.model.name.table, this.model.name) match {
        case ("COFFEES", "COF_NAME") => "name"
        case ("COFFEES", "SUP_ID") => "supplierId"
        case ("SUPPLIERS", "SUP_ID") => "id"
        case ("SUPPLIERS", "SUP_NAME") => "name"
        case ("COF_INVENTORY", "QUAN") => "quantity"
        case ("COF_INVENTORY", "COF_NAME") => "coffeeName"
        case _ => super.rawName
      }
    }
  }
}

/**
  * This customizes the Slick code generator. We only do simple name mappings.
  * For a more advanced example see https://github.com/cvogt/slick-presentation/tree/scala-exchange-2013
  */
object CodeGen extends App {
  val db = MySQLProfile.api.Database.forURL(url, driver = jdbcDriver, user = user, password = passwd)
  // filter out desired tables
//  val included = Seq("deadpool_app_config")
//  val tbName = "DeadPoolAppConfig"
//
  val included = Seq("holiday_dic")
  val tbName = "HolidayDic"

//  val included = Seq("deadpool_interface_info")
//val tbName = "DeadPoolInterfaceInfo"

  val codegen = db.run {
    MySQLProfile.defaultTables.map(_.filter(t => included contains t.name.name)).flatMap(MySQLProfile.createModelBuilder(_, false).buildModel)
  }.map(new CustomizedCodeGenerator(_))

  Await.ready(
    codegen.map(_.writeToFile(
      "slick.jdbc.MySQLProfile",
      "/Users/walis/work/dwb/dtable/src/main/scala",
      "com.ww.dtable.dao.table",
      tbName,
      tbName + "Tables.scala"
    )),
    20.seconds
  )

}
