package com.pubstate.web.error.handler

import com.pubstate.exception.BadArgumentException
import com.pubstate.exception.DomainException
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
class ControllerExceptionReporter {
  private val logger = LoggerFactory.getLogger(javaClass)

  @ExceptionHandler(MissingServletRequestParameterException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun missingParameter(e: MissingServletRequestParameterException, request: HttpServletRequest): ModelAndView {
    logger.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorPage(HttpStatus.BAD_REQUEST, "Parameter missing: name=${e.parameterName}")
  }

  @ExceptionHandler(TypeMismatchException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun parameterTypeMismatch(e: TypeMismatchException, request: HttpServletRequest): ModelAndView {
    logger.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorPage(HttpStatus.BAD_REQUEST, "Parameter type mismatch: name=${e.propertyName}, requiredType=${e.requiredType}")
  }

  @ExceptionHandler(BadArgumentException::class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  fun badArgument(e: BadArgumentException, request: HttpServletRequest): ModelAndView {
    logger.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorPage(HttpStatus.UNPROCESSABLE_ENTITY, e.message)
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  fun httpMethodNotSupported(e: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ModelAndView {
    logger.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorPage(HttpStatus.METHOD_NOT_ALLOWED, e.message)
  }

  @ExceptionHandler(DomainException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun domainRuntimeException(e: DomainException): ModelAndView {
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

    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun any(e: Throwable, request: HttpServletRequest): ModelAndView {
    logger.error("URI: " + request.requestURI + "\nController error: ", e)
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.javaClass.name)
  }

  private fun errorPage(status: HttpStatus, reason: String?): ModelAndView {
    return ModelAndView("error")
        .addObject("errorCode", status.value())
        .addObject("reason", reason)
  }
}
