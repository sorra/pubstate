package com.pubstate.web.error

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/errors/")
class ErrorPageController {
  @RequestMapping("/not-found")
  fun notFound(): ModelAndView {
    return ModelAndView("error")
        .addObject("errorCode", 404)
        .addObject("reason", "Page not found")
  }
}