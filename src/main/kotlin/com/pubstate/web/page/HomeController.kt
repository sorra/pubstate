package com.pubstate.web.page

import com.pubstate.domain.service.admin.AdminService
import com.pubstate.web.base.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController : BaseController() {
  @GetMapping("/")
  fun index(): String {
    if (!AdminService.inited()) {
      return "redirect:/admin/init"
    }
    return "index"
  }
}