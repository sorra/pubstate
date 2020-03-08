package com.pubstate.config

import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.spring.template.SpringTemplateLoader
import de.neuland.jade4j.spring.view.JadeViewResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.ViewResolver
import javax.servlet.ServletContext

@Configuration
class WebViewConfig {

  @Autowired
  private lateinit var environment: Environment

  @Bean
  fun uiManifestManager(@Autowired servletContext: ServletContext): UiManifestManager {
    return UiManifestManager.initialize(servletContext)
  }

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
    configuration.sharedVariables = ViewHelpers.globals(isDevMode())
    return configuration
  }

  @Bean
  fun viewResolver(): ViewResolver {
    val viewResolver = JadeViewResolver()
    viewResolver.setConfiguration(jadeConfiguration())
    return viewResolver
  }

  private fun isDevMode(): Boolean {
    val isDev = environment.activeProfiles.contains("dev")
    if (isDev && environment.activeProfiles.contains("prod")) {
      throw IllegalStateException("Profiles can't be both dev and prod!")
    }
    return isDev
  }
}