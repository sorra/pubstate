package com.pubstate.entity

import com.avaje.ebean.annotation.SoftDelete
import com.pubstate.dto.UserBrief
import com.pubstate.dto.UserSelf
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

  companion object : BaseFind<Long, User>() {
    fun byEmail(email: String): User? = where().eq("email", email).findUnique()
  }
}