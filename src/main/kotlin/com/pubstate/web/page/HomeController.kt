package com.pubstate.web.page

import com.pubstate.web.base.BaseController
import com.pubstate.web.page.manage.ManageSystemController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController : BaseController() {

  @GetMapping("/")
  fun index(): String {
    if (!userAuthService.isSystemInitialized()) {
      return "redirect:${ManageSystemController.INIT_PATH}"
    }
    return "forward:/articles"
  }
}
