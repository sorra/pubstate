package com.pubstate.web.auth

import com.pubstate.domain.entity.LoginPass
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.util.UriUtils
import java.io.UnsupportedEncodingException
import java.time.Instant
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object Auth {
  private val logger = LoggerFactory.getLogger(Auth::class.java)

  private const val TOKEN_NAME = "web_token"

  class AuthPack(val request: HttpServletRequest,
                 val response: HttpServletResponse,
                 var uid: Long? = null)

  // Always set by servlet filter
  private fun current(): AuthPack = RequestContextHolder.currentRequestAttributes()
      .getAttribute("authPack", RequestAttributes.SCOPE_REQUEST) as AuthPack

  fun checkUid(): Long = uid() ?: throw RequireLoginException()

  fun uid(): Long? {
    val current = current()
    if (current.uid != null) {
      return current.uid
    }

    val token = current.request.cookies?.find { it.name == TOKEN_NAME }?.value ?: return null
    val loginPass = LoginPass.byId(token) ?: return null

    return if (loginPass.whenToExpire.isAfter(Instant.now())) {
      current.uid = loginPass.userId
      current.uid
    } else {
      loginPass.delete()
      null
    }
  }

  fun login(userId: Long, rememberMe: Boolean) {
    val current = current()

    current.request.getSession(false)?.invalidate()
    val tempSession = current.request.getSession(true)
    val sessionId = tempSession.id
    tempSession.invalidate()

    val activeSeconds = (if(rememberMe) 7 * 86400 else 86400)
    val whenToExpire = Instant.now().plusSeconds(activeSeconds.toLong())

    // sessionId不会重复吧? 若重复就要changeSessionId()重新生成了
    LoginPass(sessionId, userId, whenToExpire).save()
    current.uid = userId
    current.response.addCookie(Cookie(TOKEN_NAME, sessionId).apply {
      path = "/"
      maxAge = if (rememberMe) activeSeconds else -1 // -1 = transient
    })

    logger.info("User[{}] login successfully, rememberMe={}", userId, rememberMe)
  }

  fun logout() {
    val current = current()

    val cookie = current.request.cookies?.find { it.name == TOKEN_NAME }?.also {
      LoginPass.deleteById(it.value) // must delete from DB to forbid access
      it.maxAge = 0 // 0 = delete
      current.response.addCookie(it)
    }
    current.request.getSession(false)?.invalidate()


    logger.info("User[{}] logout successfully", current.uid)
    current.uid = null
  }

  fun getRedirectGoto(requestLink: String): String {
    return "goto=" + encodeLink(requestLink)
  }

  fun encodeLink(link: String): String {
    return UriUtils.encodeQueryParam(link, "ISO-8859-1")
  }

  fun decodeLink(link: String): String {
    try {
      return UriUtils.decode(link, "ISO-8859-1")
    } catch (e: UnsupportedEncodingException) {
      throw RuntimeException(e)
    }
  }
}
