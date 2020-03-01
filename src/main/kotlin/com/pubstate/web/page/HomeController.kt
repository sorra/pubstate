package com.pubstate.web.page

import com.pubstate.domain.service.ManageService
import com.pubstate.web.base.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController : BaseController() {
  @GetMapping("/")
  fun index(): String {
    if (!ManageService.inited()) {
      return "redirect:/manage/init"
    }
    return "index"
  }
}