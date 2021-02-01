package com.pubstate.web.auth

import com.pubstate.domain.entity.LoginPass
import com.pubstate.domain.entity.User
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.util.UriUtils
import java.time.Instant
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Authentication helper
 */
object Auth {
  private val logger = LoggerFactory.getLogger(Auth::class.java)

  private const val TOKEN_NAME = "web_token"

  private const val USER_SELF = "userSelf"

  data class AuthPack(val request: HttpServletRequest, val response: HttpServletResponse)

  private fun authPack(): AuthPack {
    return RequestContextHolder.currentRequestAttributes().getAttribute("authPack", RequestAttributes.SCOPE_REQUEST) as AuthPack
  }

  fun checkUid(): String = checkUser().id

  fun checkUser(): User = user() ?: throw RequireLoginException()

  fun user(): User? = getUserSelf()

  fun initialize(request: HttpServletRequest, response: HttpServletResponse) {
    request.setAttribute("authPack", AuthPack(request, response))

    resolveTokenUser(request)?.let { user ->
      setUserSelf(user)
    }
  }

  private fun resolveTokenUser(request: HttpServletRequest): User? {
    val token = request.cookies?.find { it.name == TOKEN_NAME }?.value ?: return null
    val loginPass = LoginPass.byId(token) ?: return null
    return if (loginPass.whenToExpire.isAfter(Instant.now())) {
      User.mustGet(loginPass.userId)
    } else {
      loginPass.delete()
      null
    }
  }

  private fun getUserSelf(): User? {
    return RequestContextHolder.getRequestAttributes()?.getAttribute(USER_SELF, RequestAttributes.SCOPE_REQUEST) as User?
  }

  private fun setUserSelf(user: User) {
    RequestContextHolder.currentRequestAttributes().setAttribute(USER_SELF, user, RequestAttributes.SCOPE_REQUEST)
  }

  private fun removeUserSelf() {
    RequestContextHolder.currentRequestAttributes().removeAttribute(USER_SELF, RequestAttributes.SCOPE_REQUEST)
  }

  fun login(userId: String, rememberMe: Boolean) {
    val (request, response) = authPack()

    request.getSession(false)?.invalidate()
    val sessionId = request.getSession(true).id

    val activeSeconds = (if(rememberMe) 7 * 86400 else 86400)
    val whenToExpire = Instant.now().plusSeconds(activeSeconds.toLong())

    LoginPass(sessionId, userId, whenToExpire).save()

    response.addCookie(Cookie(TOKEN_NAME, sessionId).apply {
      path = "/"
      maxAge = if (rememberMe) activeSeconds else -1 // -1 = transient
    })

    setUserSelf(User.mustGet(userId))

    logger.info("User[{}] login successfully, rememberMe={}", userId, rememberMe)
  }

  fun logout() {
    val (request, response) = authPack()

    request.cookies?.find { it.name == TOKEN_NAME }?.also {
      LoginPass.deleteById(it.value) // must delete from DB to forbid access
      it.maxAge = 0 // 0 = delete
      response.addCookie(it)
    }
    request.getSession(false)?.invalidate()

    val userId = user()?.id
    removeUserSelf()

    logger.info("User[{}] logout successfully", userId)
  }

  fun getRedirectGoto(requestLink: String): String {
    return "goto=" + encodeLink(requestLink)
  }

  fun encodeLink(link: String): String {
    return UriUtils.encodeQueryParam(link, "ISO-8859-1")
  }

  fun decodeLink(link: String): String {
    return UriUtils.decode(link, "ISO-8859-1")
  }
}
