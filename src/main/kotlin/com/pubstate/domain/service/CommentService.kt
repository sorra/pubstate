package com.pubstate.domain.service

import com.pubstate.domain.entity.Comment
import com.pubstate.domain.entity.User
import com.pubstate.domain.enum.PubType
import io.ebean.Ebean
import org.springframework.stereotype.Service

@Service
class CommentService {

  fun create(uid: String, content: String, targetType: PubType, targetId: String): Comment {
    return Comment(
        author = Ebean.getReference(User::class.java, uid),
        content = content,
        targetType = targetType,
        targetId = targetId).apply {
      save()
    }
  }
}
