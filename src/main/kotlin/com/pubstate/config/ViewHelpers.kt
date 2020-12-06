package com.pubstate.config

import com.pubstate.domain.i18n.MessageBundle
import com.pubstate.util.Settings
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

object ViewHelpers {

  private val cdn: String = Settings.getProperty("cdn")?.removeSuffix("/") ?: ""

  fun globals(isDevMode: Boolean): Map<String, *> {
    val helpers = mutableMapOf(
        "cdn" to cdn,
        "Resources" to Resources(isDevMode),
        "i18n" to MessageBundle,
        "HumanTime" to HumanTime()
    )

    return Collections.unmodifiableMap(helpers)
  }

  @Suppress("unused")
  class Resources(private val isDevMode: Boolean) {
    fun convertLibNames(names: Array<String>, suffix: String): List<String> {
      return names.map { name ->
        val min = ".min"
        if (isDevMode || name.endsWith(min)) {
          name + suffix
        } else {
          name + min + suffix
        }
      }
    }

    fun convertDistNames(names: Array<String>, suffix: String): List<String> {
      val manifest = UiManifestManager.getManifest()
      return names.map { name ->
        val fileName = name + suffix
        manifest[fileName] ?: fileName
      }
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
        return DateTimeFormatter.ofPattern("MM-dd HH:mm").withZone().format(thatTime)
      }
      return DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone().format(thatTime)
    }

    //TODO application property can override system time zone
    private fun DateTimeFormatter.withZone() = withZone(ZoneId.systemDefault())
  }
}
