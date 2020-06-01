package com.pubstate.web.page.manage

import com.pubstate.domain.entity.Article
import com.pubstate.domain.service.ArticleService
import com.pubstate.domain.service.ManageService
import com.pubstate.tool.ArticleTool
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.Paths

@Controller
@RequestMapping("/manage")
class ManageController(
    @Autowired
    private val manageService: ManageService,
    @Autowired
    private val articleService: ArticleService
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

  @RequestMapping("/import-articles")
  @ResponseBody
  fun importArticles(): String {
    val folder = articlesFolder()
    if (!folder.exists()) {
      return "Folder doesn't exist"
    }

    var count = 0
    folder.listFiles()!!
        .filter { it.name.endsWith(".json") }
        .sortedBy { it.name.removeSuffix(".json").toLong() }
        .forEach {
          count++
          ArticleTool().import(it.readText(), articleService, count.toLong())
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
    return Paths.get(System.getProperty("user.home"), "pubstate-articles").toFile()
  }
}
