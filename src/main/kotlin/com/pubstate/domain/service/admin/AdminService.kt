package com.pubstate.domain.service.admin

import com.pubstate.domain.entity.User
import com.pubstate.exception.DomainException
import com.pubstate.domain.service.HasServices
import io.ebean.Ebean
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock

@Service
class AdminService : HasServices() {

  fun init(email: String, password: String, name: String) {
    if (inited()) {
      return
    }

    if (lock.tryLock()) {
      try {
        Ebean.execute {
          if (inited()) {
            return@execute
          }
          userAuthService.signup(User(email, password, name))
        }
      } finally {
        lock.unlock()
      }
    } else {
      throw DomainException("Cannot acquire lock for init!")
    }
  }

  companion object {
    private val lock = ReentrantLock()

    fun inited() = true // User.byId(1L) != null
  }
}