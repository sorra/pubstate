package com.pubstate.domain.entity

import com.pubstate.vo.UserBrief
import com.pubstate.vo.UserInfo
import io.ebean.annotation.SoftDelete
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class User(
    @Column(unique = true)
    var email: String,
    var password: String,
    var name: String,
    var avatar: String = "",
    var intro: String = ""
) : AutoModel() {

  @SoftDelete
  var deleted: Boolean = false

  fun toBrief() = UserBrief(id, name, avatar)

  fun toInfo() = UserInfo(id, name, avatar, intro)

  companion object : BaseFinder<String, User>(User::class.java) {

    fun byEmail(email: String): User? {
      return query().where()
          .eq("email", email)
          .findOne()
    }
  }
}