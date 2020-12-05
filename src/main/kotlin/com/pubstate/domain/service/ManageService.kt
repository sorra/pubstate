package com.pubstate.domain.service

import com.pubstate.domain.entity.User
import com.pubstate.exception.DomainException
import com.pubstate.util.UniqueIdUtil
import io.ebean.Ebean
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock

@Service
class ManageService : HasServices() {

  fun init(email: String, password: String, name: String) {
    if (inited()) {
      return
    }

    val user = User(email, password, name)
    user.id = UniqueIdUtil.one()

    if (lock.tryLock()) {
      try {
        Ebean.execute {
          if (inited()) {
            return@execute
          }
          userAuthService.signup(user)
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

    fun inited() = User.byId(UniqueIdUtil.one()) != null
  }
}