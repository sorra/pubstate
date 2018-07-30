package com.pubstate.config

import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.spring.template.SpringTemplateLoader
import de.neuland.jade4j.spring.view.JadeViewResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.ViewResolver
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.servlet.ServletContext

@Configuration
class WebViewConfig {

  @Value("\${spring.profiles.active}")
  lateinit var profile: String

  @Autowired
  lateinit var servletContext: ServletContext

  @Bean
  fun templateLoader(): SpringTemplateLoader {
    val templateLoader = SpringTemplateLoader()
    templateLoader.basePath = "/WEB-INF/views/"
    templateLoader.encoding = "UTF-8"
    templateLoader.suffix = ".jade"
    return templateLoader
  }

  @Bean
  fun jadeConfiguration(): JadeConfiguration {
    val configuration = JadeConfiguration()
    if (isDev()) {
      configuration.isCaching = false
      configuration.isPrettyPrint = true
    }
    configuration.templateLoader = templateLoader()
    configuration.sharedVariables = helpers()
    return configuration
  }

  @Bean
  fun viewResolver(): ViewResolver {
    val viewResolver = JadeViewResolver()
    viewResolver.setConfiguration(jadeConfiguration())
    return viewResolver
  }

  private fun isDev() = profile == "dev"

  private fun helpers(): Map<String, *> {
    val helpers = mutableMapOf(
        "Resource" to ResourceHelper(),
        "HumanTime" to HumanTime()
    )

    if (isDev()) {
      helpers["DevMode"] = DevMode(servletContext)
    }

    return Collections.unmodifiableMap(helpers)
  }

  class ResourceHelper {
    fun get(path: String): String {
      return path
    }
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
        return "${minutes} min"
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
}