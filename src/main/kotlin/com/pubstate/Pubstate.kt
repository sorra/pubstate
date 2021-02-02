package com.pubstate

import com.pubstate.domain.service.ImageService
import com.pubstate.web.aspect.AdminAuthorizationInterceptor
import com.pubstate.web.aspect.AuthenticationInterceptor
import com.pubstate.web.aspect.DefaultModelInterceptor
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.ErrorPageRegistrar
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import java.time.Instant

/**
 * Application starter; component scan locator
 */
@SpringBootApplication
@ServletComponentScan(basePackageClasses = [PubState::class])
class PubState : WebMvcConfigurer {

  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    registry.addResourceHandler("/static/**")
        .addResourceLocations("/static/")

    registry.addResourceHandler("/images/**")
        .addResourceLocations(fileUrl(ImageService.folderPath()))
  }

  private fun fileUrl(path: String): String {
    return if (path.startsWith("/")) {
      "file:$path/"
    }
    else {
      "file:///$path/"
    }
  }

  override fun addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(AuthenticationInterceptor()).addPathPatterns("/**")
    registry.addInterceptor(DefaultModelInterceptor()).addPathPatterns("/**")
    registry.addInterceptor(AdminAuthorizationInterceptor()).addPathPatterns("/admin/**")
  }

  @Bean("error-json")
  fun errorJsonView() = MappingJackson2JsonView()

  @Bean
  fun errorPages() = ErrorPageRegistrar { registry ->
    registry.addErrorPages(ErrorPage(HttpStatus.NOT_FOUND, "/errors/not-found"))
  }

  @Bean
  fun multipartResolver() = CommonsMultipartResolver().apply {
    // Total upload size < 10MB
    setMaxUploadSize(10*1024*1024)
    // Each file size < 5MB
    setMaxUploadSizePerFile(5*1024*1024)
    // Default setting of commons-upload: file>10MB will be written to disk
  }
}

fun main(args: Array<String>) {
  SpringApplication.run(PubState::class.java, *args)

  // Show a success message in console, because production logs are not printed in console
  println("[${Instant.now()}] Server is started.")
  System.out.flush()
}
