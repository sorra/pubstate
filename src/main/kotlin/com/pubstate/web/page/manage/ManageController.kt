package com.pubstate.web.page.manage

import com.pubstate.domain.service.ManageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/manage")
class ManageController(
    @Autowired
    private val manageService: ManageService
) {
  @GetMapping("/init")
  fun initPage(model: ModelMap): String {
    model.addAttribute("inited", ManageService.inited())
    return "manage-init"
  }

  @PostMapping("/init")
  fun init(@RequestParam email: String, @RequestParam password: String, @RequestParam name: String): String {
    manageService.init(email, password, name)
    return "redirect:/manage/init"
  }
}