package com.pubstate.web.error.handler

import com.pubstate.exception.DomainException
import com.pubstate.web.auth.RequireLoginException
import com.pubstate.web.Constants
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import sage.web.error.ApiError
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(basePackages = [Constants.API_CONTROLLER_PACKAGE])
@Order(0)
class ApiExceptionHandler {
  @ExceptionHandler(RequireLoginException::class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  fun requireLogin(request: HttpServletRequest) = ApiError("Need login")

  @ExceptionHandler(DomainException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  fun domainError(exception: DomainException) = ApiError(exception.message ?: "Unknown error")
}
