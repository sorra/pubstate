package com.pubstate.web.base

import com.pubstate.vo.UserSelf
import com.pubstate.web.auth.Auth
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PageDefaultModelInterceptor : HandlerInterceptorAdapter() {

  override fun postHandle(request: HttpServletRequest, response: HttpServletResponse,
                          handler: Any?, modelAndView: ModelAndView?) {
    modelAndView?.apply {
      addObject("userSelf", userSelf())
    }
  }

  private fun userSelf(): UserSelf? = Auth.uid()?.let { silently {
//    User.byId(it)?.toSelf()
    null
  } }

  private val log = LoggerFactory.getLogger(javaClass)

  private inline fun <T> silently(f: () -> T?): T? =
      try {
        f.invoke()
      } catch (e: Exception) {
        log.error("Exception silently:", e)
        null
      }
}
