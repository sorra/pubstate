package com.pubstate.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class HomeController {
  @GetMapping("/")
  fun index(): ModelAndView {
    return ModelAndView("index")
  }
}