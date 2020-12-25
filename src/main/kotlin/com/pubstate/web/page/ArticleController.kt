package com.pubstate.web.page

import com.pubstate.domain.entity.Article
import com.pubstate.domain.enum.FormatType
import com.pubstate.web.base.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import com.pubstate.web.auth.Auth

@Controller
@RequestMapping("/articles")
class ArticleController : BaseController() {

  private fun getFormat() = param("format", "HTML")

  @GetMapping("/new")
  fun pageForCreate(): ModelAndView {
    Auth.checkUid()
    return ModelAndView("write")
        .addObject("format", getFormat())
  }

  @PostMapping
  @ResponseBody
  fun create(@RequestParam title: String,
             @RequestParam content: String,
             @RequestParam format: String,
             @RequestParam(required = false) draftId: String?): String {
    val uid = Auth.checkUid()

    val article = articleService.create(uid, title, content, format.toEnum())

    return "/articles/${article.id}"
  }

  @GetMapping("/{id}/edit")
  fun pageForEdit(@PathVariable id: String): ModelAndView {
    Auth.checkUid()

    val article = articleService.mustGet(id)

    return ModelAndView("write")
        .addObject("article", article)
        .addObject("format", getFormat())
  }

  @PostMapping("/{id}")
  @ResponseBody
  fun edit(@PathVariable id: String,
           @RequestParam title: String,
           @RequestParam content: String,
           @RequestParam format: String,
           @RequestParam(required = false) draftId: String?): String {
    val uid = Auth.checkUid()

    val article = articleService.update(uid, id, title, content, format.toEnum())

    return "/articles/${article.id}"
  }

  @GetMapping("/{id}")
  fun view(@PathVariable id: String): ModelAndView {
    val article = articleService.mustGet(id)

    return ModelAndView("article")
        .addObject("article", article)
  }

  @GetMapping
  fun list(): ModelAndView {
    val pageNum = pageNum()
    val pageSize = pageSize()

    val (articles, totalPageCount) = Article.findPageDescWithTotalPagesCount(pageNum, pageSize)

    return pagedModelAndView("articles", articles, totalPageCount, pageNum)
  }

  private fun String.toEnum(): FormatType = FormatType.valueOf(toUpperCase())
}
