package com.pubstate.web.page

import com.pubstate.entity.User
import com.pubstate.exception.BadArgumentException
import com.pubstate.exception.DomainException
import com.pubstate.web.auth.Auth
import com.pubstate.web.base.BaseController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest


@Controller
class AuthController : BaseController() {
  @GetMapping("/login")
  fun loginPage() = "login"

  @GetMapping("/signup")
  fun signupPage() = "signup"

  @PostMapping("/login")
  fun login(@RequestParam email: String,
            @RequestParam password: String,
            @RequestParam(defaultValue = "false") rememberMe: Boolean): String {
    if (email.isEmpty() || password.isEmpty()) {
      throw DomainException("Empty input!")
    }

    logger.info("Trying login email: {}", email)

    Auth.logout()
    val user = userAuthService.login(email, password)
    Auth.login(user.id, rememberMe)

    val referer = request.getHeader("referer")

    logger.info("User[{}] login successfully. HTTP referer={}", user, referer)

    val destContext = "?goto="
    val idx = referer.lastIndexOf(destContext)
    var dest: String? =
        if (idx < 0) null
        else referer.substring(idx + destContext.length, referer.length)
    if (dest != null && dest.contains(":")) {
      logger.info("Login dest got XSS URL = {}", dest)
      dest = null // Escape cross-site URL
    }

    return "redirect:" + (if (dest == null) "/" else Auth.decodeLink(dest))
  }

  @PostMapping("/signup")
  fun signup(request: HttpServletRequest,
             @RequestParam email: String,
             @RequestParam name: String,
             @RequestParam password: String,
             @RequestParam repeatPassword: String): String {
    logger.info("Try to signup with email: {}", email)

    if (email.length > 50) {
      throw EMAIL_TOO_LONG
    }
    val idxOfAt = email.indexOf('@')
    if (idxOfAt <= 0 || email.indexOf('.', idxOfAt) <= 0) {
      throw EMAIL_WRONG_FORMAT
    }

    if (password.length < 8) {
      throw PASSWORD_TOO_SHORT
    }
    if (password.length > 20) {
      if (password.contains(",")) {
        logger.error("Password contains ',' , is it a UI coding bug?")
      }
      throw PASSWORD_TOO_LONG
    }

    if (repeatPassword != password) {
      throw REPEAT_PASSWORD_NOT_MATCH
    }

    userAuthService.signup(User(email, password, name))

    login(email, password, false)

    return "redirect:/"
  }

  @RequestMapping("/logout")
  fun logout(): String {
    Auth.logout()
    logger.info("User[{}] logout successfully.", Auth.uid())
    return "redirect:/login"
  }

  companion object {
    val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    private val EMAIL_TOO_LONG = BadArgumentException("Email should be no longer than 50 characters")
    private val EMAIL_WRONG_FORMAT = BadArgumentException("Invalid email format")
    private val PASSWORD_TOO_SHORT = BadArgumentException("Password is too short (should be 8-20 characters)")
    private val PASSWORD_TOO_LONG = BadArgumentException("Password is too long (should be 8-20 characters)")
    private val REPEAT_PASSWORD_NOT_MATCH = BadArgumentException("Two entered passwords are different")
  }
}