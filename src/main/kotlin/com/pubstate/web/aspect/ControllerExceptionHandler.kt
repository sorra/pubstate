package com.pubstate.web.aspect

import com.pubstate.exception.BadArgumentException
import com.pubstate.exception.DomainException
import com.pubstate.web.auth.Auth
import com.pubstate.web.auth.RequireLoginException
import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.ModelAndView
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
@Order(org.springframework.core.Ordered.LOWEST_PRECEDENCE)
class ControllerExceptionHandler {
  private val logger = LoggerFactory.getLogger(javaClass)

  @ExceptionHandler(RequireLoginException::class)
  fun requireLogin(request: HttpServletRequest): ModelAndView {
    if (shouldRespondJson(request)) {
      return errorResponse(request, HttpStatus.UNAUTHORIZED, "Please login")
    }

    var url = request.requestURI
    if (request.queryString != null) {
      url += "?" + request.queryString
    }

    return ModelAndView("redirect:/login?" + Auth.getRedirectGoto(url))
  }

  @ExceptionHandler(MissingServletRequestParameterException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun missingParameter(e: MissingServletRequestParameterException, request: HttpServletRequest): ModelAndView {
    logger.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorResponse(request, HttpStatus.BAD_REQUEST, "Parameter missing: name=${e.parameterName}")
  }

  @ExceptionHandler(TypeMismatchException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun parameterTypeMismatch(e: TypeMismatchException, request: HttpServletRequest): ModelAndView {
    logger.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorResponse(request, HttpStatus.BAD_REQUEST, "Parameter type mismatch: name=${e.propertyName}, requiredType=${e.requiredType}")
  }

  @ExceptionHandler(BadArgumentException::class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  fun badArgument(e: BadArgumentException, request: HttpServletRequest): ModelAndView {
    logger.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorResponse(request, HttpStatus.UNPROCESSABLE_ENTITY, e.message)
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  fun httpMethodNotSupported(e: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ModelAndView {
    logger.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorResponse(request, HttpStatus.METHOD_NOT_ALLOWED, e.message)
  }

  @ExceptionHandler(DomainException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun domainException(e: DomainException, request: HttpServletRequest): ModelAndView {
    // Omit the stacktrace
    val msgBuilder = StringBuilder(e.toString())
    val stacks = e.stackTrace
    if (stacks.isNotEmpty()) msgBuilder.append("\n\tat ").append(stacks[0])

    e.cause?.apply {
      val stringWriter = StringWriter()
      printStackTrace(PrintWriter(stringWriter))
      msgBuilder.append("\n\tCaused by: ").append(stringWriter)
    }
    logger.error(msgBuilder.toString())

    return errorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, e.message)
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun any(e: Throwable, request: HttpServletRequest): ModelAndView {
    logger.error("URI: " + request.requestURI + "\nController error: ", e)
    return errorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Application server error")
  }

  private fun errorResponse(request: HttpServletRequest, status: HttpStatus, reason: String?): ModelAndView {
    val viewName = if (shouldRespondJson(request)) "error-json" else "error"

    return ModelAndView(viewName)
        .addObject("errorCode", status.value())
        .addObject("reason", reason)
  }

  private fun shouldRespondJson(request: HttpServletRequest) =
      request.requestURI.startsWith("/api/") || request.requestURI.endsWith(".ajax")
}
