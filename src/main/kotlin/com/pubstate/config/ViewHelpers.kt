package com.pubstate.config

import com.pubstate.util.Settings
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.servlet.ServletContext

object ViewHelpers {

  private val CDN: String = Settings.getProperty("cdn") ?: ""

  fun helpers(isDevMode: Boolean, servletContext: ServletContext): Map<String, *> {
    val helpers = mutableMapOf(
        "Resource" to Resource(),
        "HumanTime" to HumanTime()
    )

    if (isDevMode) {
      helpers["DevMode"] = DevMode(servletContext)
    }

    return Collections.unmodifiableMap(helpers)
  }

  class Resource {
    fun get(path: String): String {
      return CDN + path
    }
  }

  class HumanTime {
    fun render(time: Date?): String {
      if (time == null) {
        return ""
      }
      val instant = Instant.ofEpochMilli(time.time)
      val minutes = instant.until(Instant.now(), ChronoUnit.MINUTES)
      val thatTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
      val thatDay = thatTime.toLocalDate()

      if (minutes == 0L) {
        return "Just now"
      }
      if (minutes in 1..59) {
        return "${minutes} min ago"
      }
      if (LocalDate.now().isEqual(thatDay)) { // In this day
        return DateTimeFormatter.ofPattern("HH:mm").withZone().format(thatTime)
      }
      if (LocalDate.now().year == thatDay.year) { // In this year
        return DateTimeFormatter.ofPattern("MM/dd HH:mm").withZone().format(thatTime)
      }
      return DateTimeFormatter.ofPattern("yyyy MM/dd HH:mm").withZone().format(thatTime)
    }

    //TODO application property can override system time zone
    private fun DateTimeFormatter.withZone() = withZone(ZoneId.systemDefault())
  }

  class DevMode(servletContext: ServletContext) {

    private val cssPath = "/static/css/"
    private val jsPath = "/static/js/"

    private val cssRoot = File(servletContext.getRealPath(cssPath))
    private val jsRoot = File(servletContext.getRealPath(jsPath))

    fun cssFiles(): List<String> = fileEntries(cssPath, cssRoot, ".css")

    fun jsFiles(): List<String> = fileEntries(jsPath, jsRoot, ".js")

    private fun fileEntries(rootPath: String, root: File, suffix: String): List<String> {
      return root.resolve("entries.list").readLines().filter {
        it.isNotBlank()
      }.map {
        rootPath + it + suffix
      }
    }
  }

}