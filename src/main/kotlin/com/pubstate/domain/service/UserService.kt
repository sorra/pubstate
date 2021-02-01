package com.pubstate.domain.service

import com.pubstate.domain.entity.User
import org.springframework.stereotype.Service

@Service
class UserService {

  fun findById(id: String): User {
    return User.mustGet(id)
  }
}
