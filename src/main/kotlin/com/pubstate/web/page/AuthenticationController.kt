package com.pubstate.web.page

import com.pubstate.domain.entity.User
import com.pubstate.domain.i18n.MessageBundle
import com.pubstate.exception.BadArgumentException
import com.pubstate.exception.DomainException
import com.pubstate.web.BaseController
import com.pubstate.web.auth.Auth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest


@Controller
class AuthenticationController : BaseController() {

  @GetMapping("/login")
  fun loginPage(@RequestParam(required = false) goto: String?, modelMap: ModelMap): String {
    if (goto != null) {
      modelMap.addAttribute("goto", goto)
    }
    return "login"
  }

  @GetMapping("/signup")
  fun signupPage() = "signup"

  @PostMapping("/login")
  fun login(@RequestParam email: String,
            @RequestParam password: String,
            @RequestParam(defaultValue = "false") rememberMe: Boolean): String {
    if (email.isEmpty() || password.isEmpty()) {
      throw DomainException(MessageBundle.getMessage("login_credential_empty"))
    }

    logger.info("Trying login email: {}", email)

    Auth.logout()
    val user = userAuthService.checkLoginCredential(email, password)
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
      throw BadArgumentException(MessageBundle.getMessage("email_too_long"))
    }
    val idxOfAt = email.indexOf('@')
    if (idxOfAt <= 0 || email.indexOf('.', idxOfAt) <= 0) {
      throw BadArgumentException(MessageBundle.getMessage("email_invalid_format"))
    }

    if (password.length < 8) {
      throw BadArgumentException(MessageBundle.getMessage("password_too_short"))
    }
    if (password.length > 20) {
      if (password.contains(",")) {
        logger.error("Password contains ',' , is it a UI coding bug?")
      }
      throw BadArgumentException(MessageBundle.getMessage("password_too_long"))
    }

    if (repeatPassword != password) {
      throw BadArgumentException(MessageBundle.getMessage("repeat_password_mismatch"))
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
    val logger: Logger = LoggerFactory.getLogger(AuthenticationController::class.java)
  }
}
