package com.pubstate.web.page

import com.pubstate.web.BaseController
import com.pubstate.web.auth.Auth
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/users")
class UserController : BaseController() {

  @GetMapping("/self")
  fun self(): String {
    val uid = Auth.checkUid()

    return "forward:/users/$uid"
  }

  @GetMapping("/{id}")
  fun user(@PathVariable id: String): ModelAndView {

    val userInfo = userService.findById(id).toInfo()
    val userArticles = articleService.listByAuthor(id)

    return ModelAndView("user")
        .addObject("user", userInfo)
        .addObject("articles", userArticles)
  }
}
