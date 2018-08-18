package com.pubstate.web.page.admin

import com.pubstate.entity.Article
import com.pubstate.web.base.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/admin/articles")
class ArticleAdminController : BaseController() {
  @GetMapping
  fun list(): ModelAndView {
    val pageNum = pageNum()
    val pageSize = pageSize()

    val (articles, totalPageCount) = Article.findPageDescWithTotalPageCount(pageNum, pageSize)

    return pagedModelAndView("admin-articles", "articles", articles, totalPageCount, pageNum)
  }
}