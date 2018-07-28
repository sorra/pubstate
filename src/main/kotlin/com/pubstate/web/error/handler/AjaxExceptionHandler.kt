package com.pubstate.web.error.handler

import com.pubstate.exception.DomainException
import com.pubstate.web.auth.RequireLoginException
import com.pubstate.web.util.Constants
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import sage.web.error.AjaxError
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(basePackages = [Constants.AJAX_CONTROLLER_PACKAGE])
@Order(0)
class AjaxExceptionHandler {
  @ExceptionHandler(RequireLoginException::class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  fun requireLogin(request: HttpServletRequest) = AjaxError("Need login")

  @ExceptionHandler(DomainException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  fun domainError(exception: DomainException) = AjaxError(exception.message ?: "Unknown error")
}
