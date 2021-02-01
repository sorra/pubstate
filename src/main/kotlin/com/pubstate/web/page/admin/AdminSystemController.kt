package com.pubstate.web.page.admin

import com.pubstate.domain.entity.Article
import com.pubstate.domain.entity.User
import com.pubstate.domain.helper.SystemHelper
import com.pubstate.domain.service.ArticleService
import com.pubstate.domain.service.UserAuthService
import com.pubstate.tool.ArticleTool
import com.pubstate.util.UniqueIdUtil
import com.pubstate.web.auth.Authenticated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.nio.file.Paths

@Authenticated
@Controller
@RequestMapping("/admin/system")
class AdminSystemController
@Autowired constructor(
    private val userAuthService: UserAuthService,
    private val articleService: ArticleService
) {

  @Authenticated(false)
  @GetMapping("/init")
  fun initPage(): ModelAndView {
    if (SystemHelper.isInitialized()) {
      return pageNotFound()
    }

    return ModelAndView("admin-init")
        .addObject("formAction", INIT_PATH)
  }

  @Authenticated(false)
  @PostMapping("/init")
  fun init(@RequestParam email: String, @RequestParam password: String, @RequestParam name: String): ModelAndView {
    if (SystemHelper.isInitialized()) {
      return pageNotFound()
    }

    val user = User(email, password, name)
    user.id = UniqueIdUtil.initial()
    userAuthService.signup(user)

    return ModelAndView("redirect:/")
  }

  private fun pageNotFound(): ModelAndView {
    return ModelAndView("error")
        .addObject("errorCode", HttpStatus.NOT_FOUND.value())
        .addObject("reason", "Page not found")
  }

  @RequestMapping("/import-articles")
  @ResponseBody
  fun importArticles(): String {
    val folder = articlesFolder()
    if (!folder.exists()) {
      return "Folder doesn't exist"
    }

    val batchFileName = "articles.json"

    var count = 0
    folder.listFiles()!!
        .filter { it.name.endsWith(".json") && it.name != batchFileName }
        .forEach {
          count++
          ArticleTool().import(it.readText(), articleService)
        }
    if (count == 0) {
      val batchFile = folder.resolve(batchFileName)
      if (batchFile.exists()) {
        count = ArticleTool().importBatch(batchFile.readText(), articleService)
      }
    }
    return "Imported $count items"
  }

  @RequestMapping("/export-articles")
  @ResponseBody
  fun exportArticles(): String {
    val folder = articlesFolder()
    if (!folder.exists()) {
      folder.mkdir()
    }

    var count = 0
    Article.query().findEach {
      val json = ArticleTool().export(it)
      folder.resolve("${it.id}.json").writeText(json)
      count++
    }
    return "Exported $count items"
  }

  private fun articlesFolder(): File {
    return Paths.get(System.getProperty("user.home"), "pubstate", "articles").toFile()
  }

  companion object {
    const val INIT_PATH = "/admin/system/init"
  }
}
