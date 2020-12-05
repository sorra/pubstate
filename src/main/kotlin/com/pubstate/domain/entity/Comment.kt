package com.pubstate.domain.entity

import com.pubstate.vo.CommentInfo
import com.pubstate.domain.enum.PubType
import io.ebean.PagedList
import io.ebean.annotation.SoftDelete
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Comment(
    @ManyToOne(optional = false)
    var author: User,

    @Column(columnDefinition = TEXT_COLUMN_DEF)
    val content: String,

    val targetType: PubType,

    val targetId: String
) : AutoModel() {

  @SoftDelete
  var deleted: Boolean = false

  fun toInfo() = CommentInfo(
      id, whenCreated.orDefault(), content, author.toBrief(), targetType, targetId)

  companion object : BaseFinder<String, Comment>(Comment::class.java) {

    fun commentsOf(targetType: PubType, targetId: String,
                   pageNum: Int, pageSize: Int): PagedList<Comment> {
      return query().where()
          .eq("targetType", targetType)
          .eq("targetId", targetId)
          .query().findPageDesc(pageNum, pageSize)
    }
  }
}
