package com.pubstate.service

import com.pubstate.entity.Comment
import com.pubstate.entity.PubType
import com.pubstate.entity.User
import org.springframework.stereotype.Service

@Service
class CommentService {
  fun create(uid: Long, content: String, targetType: PubType, targetId: Long): Comment {
    return Comment(content, User.ref(uid), targetType, targetId).apply {
      save()
    }
  }
}
