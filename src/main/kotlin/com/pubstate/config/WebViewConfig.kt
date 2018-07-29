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
    val helpers = mutableMapOf<String, Any>(
        "Resource" to ResourceHelper()
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
}