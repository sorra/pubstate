package com.pubstate.domain.service

import com.pubstate.domain.entity.User
import com.pubstate.exception.DomainException
import com.pubstate.util.UniqueIdUtil
import org.jasypt.util.password.StrongPasswordEncryptor
import org.springframework.stereotype.Service

@Service
class UserAuthService {

  fun isSystemInitialized() = User.byId(UniqueIdUtil.one()) != null

  fun checkLogin(email: String, password: String): User {
    val user = User.byEmail(email)
        ?: throw DomainException("Login failed: no matching user for email: %s", email)

    if (checkPassword(password, user.password)) {
      return user
    } else {
      throw DomainException("Login failed: incorrect email or password")
    }
  }

  fun signup(user: User): String {
    if (!isSystemInitialized() && user.id != UniqueIdUtil.one()) {
      throw DomainException("First time to run the system, need to initialize")
    }

    if (User.byEmail(user.email) != null) {
      throw DomainException("Signup failed: email %s is already used", user.email)
    }

    user.password = encryptPassword(user.password)
    user.save()

    if(user.name.isBlank()) user.name = "u" + user.id
    user.update()

    return user.id
  }

  fun updatePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
    val user = User.mustGet(userId)
    if (checkPassword(oldPassword, user.password)) {
      user.password = encryptPassword(newPassword)
      user.update()
      return true
    } else {
      return false
    }
  }

  fun resetPassword(userId: String, newPassword: String) {
    val user = User.mustGet(userId)
    user.password = encryptPassword(newPassword)
    user.update()
  }

  private fun checkPassword(plainPassword: String, encryptedPassword: String): Boolean {
    return StrongPasswordEncryptor().checkPassword(plainPassword, encryptedPassword)
  }

  private fun encryptPassword(plainPassword: String): String {
    return StrongPasswordEncryptor().encryptPassword(plainPassword)
  }
}
