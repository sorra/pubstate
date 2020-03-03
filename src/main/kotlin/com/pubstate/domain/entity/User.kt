package com.pubstate.domain.entity

import com.pubstate.vo.UserBrief
import com.pubstate.vo.UserSelf
import io.ebean.annotation.SoftDelete
import javax.persistence.Entity

@Entity
class User(
    var email: String,
    var password: String,
    var name: String,
    var avatar: String = "",
    var intro: String = ""
) : AutoModel() {

  @SoftDelete
  var deleted: Boolean = false

  fun toBrief() = UserBrief(id, name, avatar)

  fun toSelf() = UserSelf(id, name, avatar)

  companion object : BaseFinder<Long, User>(User::class.java) {

    fun byEmail(email: String): User? {
      return query().where()
          .eq("email", email)
          .findOne()
    }
  }
}