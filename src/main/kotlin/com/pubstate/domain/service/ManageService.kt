package com.pubstate.domain.service

import com.pubstate.domain.entity.User
import com.pubstate.domain.i18n.MessageBundle
import com.pubstate.exception.DomainException
import com.pubstate.util.UniqueIdUtil
import io.ebean.Ebean
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock

@Service
class ManageService : HasServices() {

  fun intialize(email: String, password: String, name: String) {
    if (isInitialized()) {
      return
    }

    val user = User(email, password, name)
    user.id = UniqueIdUtil.initial()

    if (lock.tryLock()) {
      try {
        Ebean.execute {
          if (isInitialized()) {
            return@execute
          }
          userAuthService.signup(user)
        }
      } finally {
        lock.unlock()
      }
    } else {
      throw DomainException(MessageBundle.getMessage("init_lock_failed"))
    }
  }
  
  fun isInitialized() = userAuthService.isSystemInitialized()

  companion object {
    private val lock = ReentrantLock()
  }
}