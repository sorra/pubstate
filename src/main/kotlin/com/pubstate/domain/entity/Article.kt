package com.pubstate.domain.entity

import com.pubstate.domain.enum.FormatType
import io.ebean.Model
import io.ebean.annotation.SoftDelete
import java.sql.Timestamp
import javax.persistence.*

@Entity
class Article(
    @Id @GeneratedValue
    var id: Long = 0,
    @Version
    var version: Long = 0,

    var whenCreated: Timestamp = Timestamp(System.currentTimeMillis()),

    var whenModified: Timestamp = whenCreated,

    var title: String,

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = TEXT_COLUMN_DEF)
    var inputContent: String,

    @Column(columnDefinition = TEXT_COLUMN_DEF)
    var outputContent: String,

    var formatType: FormatType,

    @ManyToOne(optional = false)
    var author: User
) : Model() {

  @SoftDelete
  var deleted: Boolean = false

  companion object : BaseFinder<Long, Article>(Article::class.java) {
    fun listByAuthor(authorId: Long): List<Article> {
      return query().where()
          .eq("author", User.ref(authorId))
          .findList()
    }
  }
}