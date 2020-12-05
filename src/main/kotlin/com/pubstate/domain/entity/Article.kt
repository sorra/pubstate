package com.pubstate.domain.entity

import com.pubstate.domain.enum.FormatType
import io.ebean.annotation.SoftDelete
import javax.persistence.*

@Entity
class Article(
    @ManyToOne(optional = false)
    var author: User,

    var title: String,

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = TEXT_COLUMN_DEF)
    var inputContent: String,

    @Column(columnDefinition = TEXT_COLUMN_DEF)
    var outputContent: String,

    var formatType: FormatType
) : AutoModel() {

  @SoftDelete
  var deleted: Boolean = false

  companion object : BaseFinder<String, Article>(Article::class.java) {
    fun listByAuthor(authorId: String): List<Article> {
      return query().where()
          .eq("author", User.ref(authorId))
          .findList()
    }
  }
}
