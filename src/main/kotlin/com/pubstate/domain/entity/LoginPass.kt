package com.pubstate.domain.entity

import io.ebean.Model
import io.ebean.annotation.WhenCreated
import io.ebean.annotation.WhenModified
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Version

@Entity
class LoginPass(
    @Id
    var passId: String,

    @Column(nullable = false)
    var userId: Long,

    var whenToExpire: Instant
) : Model() {
  @Version
  var version: Long = 0

  @WhenCreated
  var whenCreated: Timestamp? = null

  @WhenModified
  var whenModified: Timestamp? = null

  companion object : BaseFinder<String, LoginPass>(LoginPass::class.java)
}