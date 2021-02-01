package com.pubstate.web.page

import com.pubstate.domain.entity.Article
import com.pubstate.domain.entity.Draft
import com.pubstate.domain.entity.User
import com.pubstate.domain.enum.FormatType
import com.pubstate.domain.permission.DraftPermission
import com.pubstate.web.auth.Auth
import com.pubstate.web.BaseController
import com.pubstate.web.auth.Authenticated
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@Authenticated
@Controller
@RequestMapping("/drafts")
class DraftController : BaseController() {

  @GetMapping
  fun list(): ModelAndView {
    val drafts = Draft.listByAuthor(Auth.checkUid())
    return ModelAndView("drafts").addObject("drafts", drafts)
  }

  @GetMapping("/{id}")
  fun resume(@PathVariable id: String): ModelAndView {
    val draft = Draft.mustGet(id)
    val article = if (draft.targetId.isEmpty()) {
      Article(
          title = draft.title,
          inputContent = draft.inputContent,
          outputContent = "",
          formatType = draft.formatType,
          author = Auth.checkUser()
      )
    } else {
      Article.mustGet(draft.targetId).apply {
        title = draft.title
        inputContent = draft.inputContent
        formatType = draft.formatType
      }
    }

    return ModelAndView("write")
        .addObject("article", article)
        .addObject("draftId", id)
  }

  @PostMapping("/save")
  @ResponseBody
  fun save(@RequestParam(required = false) draftId: String?,
           @RequestParam(required = false) targetId: String?,
           @RequestParam(defaultValue = "") title: String,
           @RequestParam(defaultValue = "") content: String,
           @RequestParam format: String): String {
    if (title.isBlank() && content.isBlank()) {
      return ""
    }

    return draftId?.let {
      Draft.byId(it)
    }?.let { draft ->
      DraftPermission(Auth.checkUid(), draft).canEdit()
      draft.title = title
      draft.inputContent = content
      draft.formatType = format.toEnum()

      draft.update()
      draft.id
    } ?: let {
      val draft = Draft(
          author = Auth.checkUser(),
          targetId = targetId ?: "",
          title = title,
          inputContent = content,
          formatType = format.toEnum())

      draft.save()
      draft.id
    }
  }

  @PostMapping("/{id}/delete")
  @ResponseBody
  fun delete(@PathVariable id: String) {
    val uid = Auth.checkUid()
    val draft = Draft.mustGet(id)

    DraftPermission(uid, draft).canDelete()
    draft.delete()
  }

  private fun String.toEnum(): FormatType = FormatType.valueOf(toUpperCase())
}
