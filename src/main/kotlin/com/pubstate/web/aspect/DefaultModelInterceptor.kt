package com.pubstate.web.aspect

import com.pubstate.web.auth.Auth
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Populates default model attributes to every view
 */
class DefaultModelInterceptor : HandlerInterceptor {

  override fun postHandle(request: HttpServletRequest, response: HttpServletResponse,
                          handler: Any?, modelAndView: ModelAndView?) {
    modelAndView?.apply {
      addObject("userSelf", Auth.user())
    }
  }
}
