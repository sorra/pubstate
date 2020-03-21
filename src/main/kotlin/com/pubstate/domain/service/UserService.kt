package com.pubstate.domain.service

import com.pubstate.domain.entity.User
import com.pubstate.web.auth.Auth
import org.springframework.stereotype.Service

@Service
class UserService {

  fun currentUser(): User {
    val uid = Auth.checkUid()
    return findById(uid)
  }

  fun findById(id: Long): User {
    return User.mustGet(id)
  }
}
