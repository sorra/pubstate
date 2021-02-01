package com.pubstate.web.page

import com.pubstate.domain.helper.SystemHelper
import com.pubstate.web.BaseController
import com.pubstate.web.page.admin.AdminSystemController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController : BaseController() {

  @GetMapping("/")
  fun index(): String {
    if (!SystemHelper.isInitialized()) {
      return "redirect:${AdminSystemController.INIT_PATH}"
    }
    return "forward:/articles"
  }
}
