package com.pubstate.web.page.admin

import com.pubstate.service.admin.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminController(
    @Autowired
    private val adminService: AdminService
) {
  @GetMapping("/init")
  fun initPage(model: ModelMap): String {
    model.addAttribute("inited", AdminService.inited())
    return "admin-init"
  }

  @PostMapping("/init")
  fun init(@RequestParam email: String, @RequestParam password: String, @RequestParam name: String): String {
    adminService.init(email, password, name)
    return "redirect:/admin/init"
  }
}