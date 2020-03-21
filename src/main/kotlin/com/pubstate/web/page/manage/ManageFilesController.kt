package com.pubstate.web.page.manage

import com.pubstate.domain.entity.FileItem
import com.pubstate.web.base.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/manage/files")
class ManageFilesController : BaseController() {

  @GetMapping
  fun list(): ModelAndView {
    val pageNum = pageNum()
    val pageSize = pageSize()

    val (files, totalPagesCount) = FileItem.findPageDescWithTotalPagesCount(pageNum, pageSize)

    return pagedModelAndView("manage-files", "files", files, totalPagesCount, pageNum)
  }
}