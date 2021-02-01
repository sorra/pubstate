package com.pubstate.web.page.admin

import com.pubstate.domain.entity.Image
import com.pubstate.web.BaseController
import com.pubstate.web.auth.Authenticated
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Authenticated
@Controller
@RequestMapping("/admin/files")
class AdminFilesController : BaseController() {

  @GetMapping
  fun list(): ModelAndView {
    val pageNum = pageNum()
    val pageSize = pageSize()

    val (files, totalPagesCount) = Image.findPageDescWithTotalPagesCount(pageNum, pageSize)

    return pagedModelAndView("admin-files", "files", files, totalPagesCount, pageNum)
  }
}