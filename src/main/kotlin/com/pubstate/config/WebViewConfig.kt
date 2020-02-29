package com.pubstate.config

import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.spring.template.SpringTemplateLoader
import de.neuland.jade4j.spring.view.JadeViewResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.ViewResolver
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
    if (isDevMode()) {
      configuration.isCaching = false
      configuration.isPrettyPrint = true
    }
    configuration.templateLoader = templateLoader()
    configuration.sharedVariables = ViewHelpers.helpers(isDevMode(), servletContext)
    return configuration
  }

  @Bean
  fun viewResolver(): ViewResolver {
    val viewResolver = JadeViewResolver()
    viewResolver.setConfiguration(jadeConfiguration())
    return viewResolver
  }

  private fun isDevMode() = profile == "dev"
}