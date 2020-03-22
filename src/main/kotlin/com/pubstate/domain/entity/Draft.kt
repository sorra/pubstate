package com.pubstate.domain.entity

import com.pubstate.domain.enum.FormatType
import io.ebean.Finder
import javax.persistence.*

@Entity
class Draft(
    var targetId: Long,

    var title: String,

    @Column(columnDefinition = "TEXT")
    var inputContent: String,

    var formatType: FormatType,

    @ManyToOne(optional = false)
    val author: User) : AutoModel() {

  companion object : Finder<Long, Draft>(Draft::class.java) {
    fun listByAuthor(authorId: Long): List<Article> {
      return Article.query().where()
          .eq("author", User.ref(authorId))
          .findList()
    }
  }
}
