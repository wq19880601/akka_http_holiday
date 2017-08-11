package com.ww.dtable.dao.table

import com.ww.dtable.domain.HolidayDicRow
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object HolidayDic extends {
  val profile = slick.jdbc.MySQLProfile
} with HolidayDic

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait HolidayDic {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = holidayDicTable.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** GetResult implicit for fetching HolidayDicRow objects using plain SQL queries */
  implicit def GetResultHolidayDicRow(implicit e0: GR[Option[Int]], e1: GR[Int], e2: GR[Byte], e3: GR[DateTime], e4: GR[Option[String]]): GR[HolidayDicRow] = GR{
    prs => import prs._
    HolidayDicRow.tupled((<<?[Int], <<[Int], <<[Byte], <<[DateTime], <<[Byte], <<?[String], <<[DateTime], <<[DateTime]))
  }
  /** Table description of table holiday_dic. Objects of this class serve as prototypes for rows in queries. */
  class HolidayDicTable(_tableTag: Tag) extends profile.api.Table[HolidayDicRow](_tableTag, Some("workorderdb"), "holiday_dic") {
    def * = (Rep.Some(id), hYear, hMonth, hCurrentDate, isHoliday, holidayDesc, createTime, modifyTime) <> (HolidayDicRow.tupled, HolidayDicRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(hYear), Rep.Some(hMonth), Rep.Some(hCurrentDate), Rep.Some(isHoliday), holidayDesc, Rep.Some(createTime), Rep.Some(modifyTime)).shaped.<>({r=>import r._; _1.map(_=> HolidayDicRow.tupled((_1, _2.get, _3.get, _4.get, _5.get, _6, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val hYear: Rep[Int] = column[Int]("h_year")
    val hMonth: Rep[Byte] = column[Byte]("h_month")
    val hCurrentDate: Rep[DateTime] = column[DateTime]("h_current_date")
    val isHoliday: Rep[Byte] = column[Byte]("is_holiday")
    val holidayDesc: Rep[Option[String]] = column[Option[String]]("holiday_desc", O.Length(50,varying=true), O.Default(None))
    val createTime: Rep[DateTime] = column[DateTime]("create_time")
    val modifyTime: Rep[DateTime] = column[DateTime]("modify_time")

    /** Uniqueness Index over (hCurrentDate) (database name idx_currentdate) */
    val index1 = index("idx_currentdate", hCurrentDate, unique=true)
  }
  lazy val holidayDicTable = new TableQuery(tag => new HolidayDicTable(tag))
}
