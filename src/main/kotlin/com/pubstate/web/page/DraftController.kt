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
  fun resume(@PathVariable id: Long): ModelAndView {
    val currentUser = userService.currentUser()

    val article = Draft.byId(id)?.let { draft ->
      if (draft.targetId > 0) {
        Article.mustGet(draft.targetId).apply {
          title = draft.title
          inputContent = draft.inputContent
          formatType = draft.formatType
        }
      } else {
        Article(
            title = draft.title,
            inputContent = draft.inputContent,
            outputContent = "",
            formatType = draft.formatType,
            author = currentUser
        )
      }
    } ?: throw DomainException("Draft[$id] does not exist")

    return ModelAndView("write")
        .addObject("artcle", article)
        .addObject("draftId", id)
  }

  @PostMapping("/save")
  @ResponseBody
  fun save(@RequestParam(required = false) draftId: Long?,
           @RequestParam(required = false) targetId: Long?,
           @RequestParam(defaultValue = "") title: String,
           @RequestParam(defaultValue = "") content: String,
           @RequestParam format: String): Long {
    val uid = Auth.checkUid()

    if (title.isBlank() && content.isBlank()) {
      return 0L
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
          targetId = targetId ?: 0,
          title = title,
          inputContent = content,
          formatType = format.toEnum(),
          author = User.ref(uid))

      draft.save()
      draft.id
    }
  }

  private fun String.toEnum(): FormatType = FormatType.valueOf(toUpperCase())
}
