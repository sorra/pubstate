package com.pubstate.web.page.manage

import com.pubstate.domain.entity.Article
import com.pubstate.web.base.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/manage/articles")
class ManageArticlesController : BaseController() {

  @GetMapping
  fun list(): ModelAndView {
    val pageNum = pageNum()
    val pageSize = pageSize()

    val (articles, totalPagesCount) = Article.findPageDescWithTotalPagesCount(pageNum, pageSize)

    return pagedModelAndView("manage-articles", "articles", articles, totalPagesCount, pageNum)
  }
}