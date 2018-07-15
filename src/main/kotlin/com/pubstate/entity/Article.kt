package com.pubstate.entity

import com.avaje.ebean.annotation.SoftDelete
import javax.persistence.*

@Entity
class Article(var title: String,
              @Column(columnDefinition = "TEXT") @Basic(fetch = FetchType.LAZY)
              var inputContent: String,
              @Column(columnDefinition = "TEXT")
              var outputContent: String,
              var formatType: FormatType,
              @ManyToOne(optional = false)
              var author: User
) : AutoModel() {

  @SoftDelete
  var deleted: Boolean = false

  companion object : BaseFind<Long, Article>()
}