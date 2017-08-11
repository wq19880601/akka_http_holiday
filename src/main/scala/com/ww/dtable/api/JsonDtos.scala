package com.ww.dtable.api

import org.joda.time.DateTime

case class TeamName(name: String) extends AnyVal

case class UserName(name: String) extends AnyVal






case class Error(
                  status: Int,
                  title: String,
                  errorType: String,
                  detail: Option[String] = None)

case class Pagination(offset: Int, limit: Int)
