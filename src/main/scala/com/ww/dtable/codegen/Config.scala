package com.ww.dtable.codegen

object Config {
  val url = "jdbc:mysql://127.0.0.1/workorderdb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"
  val jdbcDriver = "com.mysql.jdbc.Driver"
  val slickProfile = slick.jdbc.MySQLProfile
  val user = "root"
  val passwd = "root"
}
