package com.pubstate.web.aspect

import com.pubstate.web.auth.Auth
import com.pubstate.web.auth.AuthenticatedUtil
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Authenticate when required.
 * Why HandlerInterceptor instead of Filter?
 * 1. more secure, effective for servlet "forward" and "include"
 * 2. only authenticate handlers, no need to authenticate other things
 * 3. need to the handler object to lookup @Authenticated annotation
 */
class AuthenticationInterceptor : HandlerInterceptor {

  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    request.setAttribute("authPack", Auth.AuthPack(request, response))

    if (handler is HandlerMethod &&
        AuthenticatedUtil.shouldAuthenticate(handler)) {
      Auth.checkUid()
    }

    return true
  }
}
