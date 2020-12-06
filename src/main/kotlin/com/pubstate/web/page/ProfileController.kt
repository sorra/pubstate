package com.pubstate.web.page

import com.pubstate.domain.i18n.MessageBundle
import com.pubstate.exception.BadArgumentException
import com.pubstate.web.base.BaseController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/profile")
class ProfileController : BaseController() {

  @GetMapping
  fun page(): ModelAndView {
    val currentUser = userService.currentUser()

    return ModelAndView("profile")
        .addObject("user", currentUser)
  }

  @PostMapping
  @ResponseBody
  fun update(@RequestParam name: String?, @RequestParam intro: String?) {
    val currentUser = userService.currentUser()

    val updatedAttrs = mutableListOf<String>()
    if (name != null) {
      if (name.isBlank()) {
        throw BadArgumentException(MessageBundle.getMessage("user_name_blank"))
      }
      currentUser.name = name
      updatedAttrs += "name"
    }

    if (intro != null) {
      currentUser.intro = intro
      updatedAttrs += "intro"
    }

    currentUser.update()
    logger.info("User[{}] updated profile: {}", currentUser.id, updatedAttrs)
  }


  @GetMapping("/avatar")
  fun avatar(): ModelAndView {
    val currentUser = userService.currentUser()

    return ModelAndView("change-avatar")
        .addObject("user", currentUser)
  }

  @PostMapping("/avatar")
  fun updateAvatar(
      @RequestParam(required = false) avatar: MultipartFile?,
      @RequestParam(required = false) color: String?): String {
    val currentUser = userService.currentUser()

    when {
      avatar != null && !avatar.isEmpty -> {
        val fileName = imageService.upload(currentUser.id, avatar, isAvatar = true)
        currentUser.avatar = fileName
      }
      color != null && color.isNotBlank() -> {
        currentUser.avatar = "color${color}.png"
      }
      else -> {
        throw BadArgumentException(MessageBundle.getMessage("avatar_not_set"))
      }
    }
    currentUser.update()

    return "redirect:/profile"
  }

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(ProfileController::class.java)
  }
}
