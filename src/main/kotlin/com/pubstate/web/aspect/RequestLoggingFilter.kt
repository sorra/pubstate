package com.pubstate.web.aspect

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.concurrent.atomic.AtomicLong
import javax.annotation.Priority
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest

@WebFilter
@Priority(0)
class RequestLoggingFilter : javax.servlet.Filter {
  private val logger = LoggerFactory.getLogger(javaClass)

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    MDC.put("requestId", requestIdCounter.incrementAndGet().toString())

    val name = getRequestName(request)

    logger.info("Start request: {}", name)

    val timeStart = System.currentTimeMillis()
    try {
      chain.doFilter(request, response)
    } finally {
      val timeCost = System.currentTimeMillis() - timeStart
      logger.info("End request: {}ms {}", timeCost, name)
      MDC.remove("requestId")
    }
  }

  private fun getRequestName(request: ServletRequest): String? {
    return if (request is HttpServletRequest) {
      val query = if (request.queryString != null) "?" + request.queryString else ""
      request.protocol + " " + request.method + " " + request.requestURI + query
    } else {
      request.protocol
    }
  }

  override fun init(filterConfig: FilterConfig) {}

  override fun destroy() {}

  companion object {
    private val requestIdCounter: AtomicLong = AtomicLong()
  }
}