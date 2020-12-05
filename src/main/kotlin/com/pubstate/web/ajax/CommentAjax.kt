package com.pubstate.web.ajax

import com.pubstate.domain.enum.PubType
import com.pubstate.web.auth.Auth
import com.pubstate.web.base.BaseController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentAjax : BaseController() {

  @GetMapping
  fun commentsOf(@RequestParam targetType: String, @RequestParam targetId: String): Map<String, *> {
//    val page = Comment.commentsOf(targetType.toEnum(), targetId, pageNum(), pageSize())

//    return mapOf(
//        "totalCount" to page.totalRowCount,
//        "list" to page.list.map(Comment::toInfo)
//    )
    return emptyMap<String, Any>()
  }

  @PostMapping
  fun create(@RequestParam content: String,
             @RequestParam targetType: String, @RequestParam targetId: String) {
    val uid = Auth.checkUid()

    commentService.create(uid, content, targetType.toEnum(), targetId)
  }

  private fun String.toEnum(): PubType = PubType.valueOf(toUpperCase())
}
