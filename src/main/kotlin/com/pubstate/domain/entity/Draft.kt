package com.pubstate.domain.entity

import com.pubstate.domain.enum.FormatType
import javax.persistence.*

@Entity
class Draft(
    @ManyToOne(optional = false)
    val author: User,

    var targetId: String,

    var title: String,

    @Column(columnDefinition = TEXT_COLUMN_DEF)
    var inputContent: String,

    var formatType: FormatType
) : AutoModel() {

  companion object : BaseFinder<String, Draft>(Draft::class.java) {
    fun listByAuthor(authorId: String): List<Draft> {
      return query().where()
          .eq("author", User.ref(authorId))
          .findList()
    }
  }
}
