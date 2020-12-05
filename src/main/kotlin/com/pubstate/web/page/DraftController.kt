package com.pubstate.web.page

import com.pubstate.domain.entity.Article
import com.pubstate.domain.entity.Draft
import com.pubstate.domain.entity.User
import com.pubstate.domain.enum.FormatType
import com.pubstate.domain.permission.DraftPermission
import com.pubstate.exception.DomainException
import com.pubstate.web.auth.Auth
import com.pubstate.web.base.BaseController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/drafts")
class DraftController : BaseController() {

  @GetMapping
  fun list(): ModelAndView {
    val uid = Auth.checkUid()
    val drafts = Draft.listByAuthor(uid)
    return ModelAndView("drafts").addObject("drafts", drafts)
  }

  @GetMapping("/{id}")
  fun resume(@PathVariable id: String): ModelAndView {
    val currentUser = userService.currentUser()

    val article = Draft.byId(id)?.let { draft ->
      if (draft.targetId.isEmpty()) {
        Article(
            title = draft.title,
            inputContent = draft.inputContent,
            outputContent = "",
            formatType = draft.formatType,
            author = currentUser
        )
      } else {
        Article.mustGet(draft.targetId).apply {
          title = draft.title
          inputContent = draft.inputContent
          formatType = draft.formatType
        }
      }
    } ?: throw DomainException("Draft[$id] does not exist")

    return ModelAndView("write")
        .addObject("artcle", article)
        .addObject("draftId", id)
  }

  @PostMapping("/save")
  @ResponseBody
  fun save(@RequestParam(required = false) draftId: String?,
           @RequestParam(required = false) targetId: String?,
           @RequestParam(defaultValue = "") title: String,
           @RequestParam(defaultValue = "") content: String,
           @RequestParam format: String): String {
    val uid = Auth.checkUid()

    if (title.isBlank() && content.isBlank()) {
      return ""
    }

    return draftId?.let {
      Draft.byId(it)
    }?.let {draft ->
      DraftPermission(uid, draft).canEdit()
      draft.title = title
      draft.inputContent = content
      draft.formatType = format.toEnum()

      draft.update()
      draft.id
    } ?: let {
      val draft = Draft(
          author = User.ref(uid),
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
