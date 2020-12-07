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
@RequestMapping("/manage/system")
class ManageSystemController(
    @Autowired
    private val manageService: ManageService,
    @Autowired
    private val articleService: ArticleService
) {

  @GetMapping("/init")
  fun initPage(model: ModelMap): String {
    model.addAttribute("inited", manageService.isInitialized())
    return "manage-init"
  }

  @PostMapping("/init")
  fun init(@RequestParam email: String, @RequestParam password: String, @RequestParam name: String): String {
    manageService.intialize(email, password, name)
    return "redirect:$INIT_PATH"
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
    const val INIT_PATH = "/manage/system/init"
  }
}
