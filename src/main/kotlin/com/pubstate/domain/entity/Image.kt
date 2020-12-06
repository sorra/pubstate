package com.pubstate.domain.entity

import io.ebean.annotation.SoftDelete
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Image(
    @ManyToOne(optional = false)
    var owner: User,
    var isAvatar: Boolean
) : AutoModel() {

  @SoftDelete
  var deleted: Boolean = false

  companion object : BaseFinder<String, Image>(Image::class.java)
}
