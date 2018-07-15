package com.pubstate.web.ajax

import com.pubstate.entity.Comment
import com.pubstate.entity.PubType
import com.pubstate.web.auth.Auth
import com.pubstate.web.base.BaseController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentAjax : BaseController() {

  @GetMapping
  fun commentsOf(@RequestParam targetType: String, @RequestParam targetId: Long): Map<String, *> {
    val page = Comment.commentsOf(targetType.toEnum(), targetId, pageNum(), pageSize())

    return mapOf(
        "totalCount" to page.totalRowCount,
        "list" to page.list.map(Comment::toInfo)
    )
  }

  @PostMapping
  fun create(@RequestParam content: String,
             @RequestParam targetType: String, @RequestParam targetId: Long) {
    val uid = Auth.checkUid()

    commentService.create(uid, content, targetType.toEnum(), targetId)
  }

  private fun String.toEnum(): PubType = PubType.valueOf(toUpperCase())
}
