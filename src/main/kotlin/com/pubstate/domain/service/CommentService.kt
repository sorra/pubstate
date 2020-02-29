package com.pubstate.domain.service

import com.pubstate.domain.entity.Comment
import com.pubstate.domain.entity.PubType
import com.pubstate.domain.entity.User
import io.ebean.Ebean
import org.springframework.stereotype.Service

@Service
class CommentService {
  fun create(uid: Long, content: String, targetType: PubType, targetId: Long): Comment {
    return Comment(content, Ebean.getReference(User::class.java, uid), targetType, targetId).apply {
      save()
    }
  }
}
