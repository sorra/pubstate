package com.pubstate.web.aspect

import com.pubstate.domain.entity.User
import com.pubstate.vo.UserSelf
import com.pubstate.web.auth.Auth
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Populates default model attributes to every page
 */
class PageDefaultModelInterceptor : HandlerInterceptor {

  override fun postHandle(request: HttpServletRequest, response: HttpServletResponse,
                          handler: Any?, modelAndView: ModelAndView?) {
    modelAndView?.apply {
      addObject("userSelf", userSelf())
    }
  }

  private fun userSelf(): UserSelf? = Auth.uid()?.let { silently {
    User.byId(it)?.toSelf()
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
