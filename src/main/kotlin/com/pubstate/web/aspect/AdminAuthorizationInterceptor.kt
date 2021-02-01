package com.pubstate.web.aspect

import com.pubstate.domain.helper.AdminHelper
import com.pubstate.util.UniqueIdUtil
import com.pubstate.web.auth.Auth
import com.pubstate.web.auth.AuthenticatedUtil
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Authorize admin role for admin area URI.
 */
class AdminAuthorizationInterceptor : HandlerInterceptor {

  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    if (isAdminUser()) {
      return true
    }

    if (handler is HandlerMethod &&
        AuthenticatedUtil.shouldAuthenticate(handler)) {
      response.sendError(HttpStatus.NOT_FOUND.value())
      return false
    }

    // The handler is responsible for authorization
    return true
  }

  private fun isAdminUser() = AdminHelper.isAdmin(Auth.uid())
}
