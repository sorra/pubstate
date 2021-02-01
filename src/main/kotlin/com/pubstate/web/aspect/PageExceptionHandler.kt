package com.pubstate.web.aspect

import com.pubstate.web.auth.Auth
import com.pubstate.web.auth.RequireLoginException
import com.pubstate.web.Constants
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.View
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(basePackages = [Constants.PAGE_CONTROLLER_PACKAGE])
@Order(0)
class PageExceptionHandler {
  @ExceptionHandler(RequireLoginException::class)
  fun redirectionPageForLogin(request: HttpServletRequest): View {
    var url = request.requestURI
    if (request.queryString != null) {
      url += "?" + request.queryString
    }

    return RedirectView("/login?" + Auth.getRedirectGoto(url))
  }
}
