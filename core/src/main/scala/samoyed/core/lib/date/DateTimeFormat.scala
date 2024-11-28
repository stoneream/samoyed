package samoyed.core.lib.date

import java.time.format.DateTimeFormatter

object DateTimeFormat {
  val ymd: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val ym: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
  val y: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
}
