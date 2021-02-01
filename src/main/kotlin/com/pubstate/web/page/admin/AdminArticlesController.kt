package com.pubstate.web.page.admin

import com.pubstate.domain.entity.Article
import com.pubstate.web.BaseController
import com.pubstate.web.auth.Authenticated
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Authenticated
@Controller
@RequestMapping("/admin/articles")
class AdminArticlesController : BaseController() {

  @GetMapping
  fun list(): ModelAndView {
    val pageNum = pageNum()
    val pageSize = pageSize()

    val (articles, totalPagesCount) = Article.findPageDescWithTotalPagesCount(pageNum, pageSize)

    return pagedModelAndView("admin-articles", "articles", articles, totalPagesCount, pageNum)
  }
}