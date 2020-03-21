package com.pubstate

import com.pubstate.domain.enum.Folder
import com.pubstate.domain.service.FileService
import com.pubstate.web.base.PageDefaultModelInterceptor
import com.pubstate.web.filter.AccessLoggingFilter
import com.pubstate.web.filter.AuthFilter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.ErrorPageRegistrar
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Instant

/**
 * Application starter; component scan locator
 */
@SpringBootApplication
class PubState : WebMvcConfigurer {

  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    registry.addResourceHandler("/static/**")
        .addResourceLocations("/static/")

    for (folder in Folder.values()) {
      registry.addResourceHandler("/files/${folder.folderName}/**")
          .addResourceLocations(fileUrl(FileService.folderFilePath(folder)))
    }
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
    registry.addInterceptor(PageDefaultModelInterceptor())
  }

  @Bean
  fun accessLoggingFilter() = AccessLoggingFilter()

  @Bean
  fun authFilter() = AuthFilter()

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
  System.out.println("[${Instant.now()}] Server is started.")
  System.out.flush()
}
