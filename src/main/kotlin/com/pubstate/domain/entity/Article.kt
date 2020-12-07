package com.pubstate.domain.entity

import com.pubstate.domain.enum.FormatType
import com.pubstate.util.UniqueIdUtil
import io.ebean.Model
import io.ebean.annotation.SoftDelete
import java.sql.Timestamp
import javax.persistence.*

@Entity
class Article(
    @Id
    @Column(columnDefinition = "CHAR(22)")
    var id: String = UniqueIdUtil.newId(),

    @Version
    var version: Long = 0,

    var whenCreated: Timestamp = Timestamp(System.currentTimeMillis()),

    var whenModified: Timestamp = whenCreated,

    @ManyToOne(optional = false)
    var author: User,

    var title: String,

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = TEXT_COLUMN_DEF)
    var inputContent: String,

    @Column(columnDefinition = TEXT_COLUMN_DEF)
    var outputContent: String,

    var formatType: FormatType
) : Model() {

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
