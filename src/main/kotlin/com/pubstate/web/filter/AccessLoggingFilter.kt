package com.pubstate.web.filter

import org.slf4j.LoggerFactory
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AccessLoggingFilter : javax.servlet.Filter {
  private val logger = LoggerFactory.getLogger(javaClass)

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val name = if (request is HttpServletRequest) {
      val query = if(request.queryString != null) "?"+request.queryString else ""
      request.protocol + " " + request.method + " " + request.requestURI + query
    } else {
      request.protocol
    }

    logger.info("Start request: {}", name)

    val timeStart = System.currentTimeMillis()
    try {
      chain.doFilter(request, response)
    } finally {
      if (name != null) {
        val timeCost = System.currentTimeMillis() - timeStart
        logger.info("End request: {}ms {}", timeCost, name)
      }
    }
  }

  override fun init(filterConfig: FilterConfig) {}

  override fun destroy() {}
}