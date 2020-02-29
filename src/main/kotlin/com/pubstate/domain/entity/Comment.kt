package com.pubstate.domain.entity

import com.pubstate.vo.CommentInfo
import com.pubstate.domain.entity.query.QComment
import io.ebean.PagedList
import io.ebean.annotation.SoftDelete
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Comment(
    @Column(columnDefinition = TEXT_COLUMN_DEF)
    val content: String,

    @ManyToOne(optional = false)
    var author: User,

    val targetType: PubType,

    val targetId: Long
) : AutoModel() {

  @SoftDelete
  var deleted: Boolean = false

  fun toInfo() = CommentInfo(
      id, whenCreated.orDefault(), content, author.toBrief(), targetType, targetId)

  companion object : BaseFinder<Long, Comment>(Comment::class.java) {

    fun commentsOf(targetType: PubType, targetId: Long,
                   pageNum: Int, pageSize: Int): PagedList<Comment> =
        QComment()
            .targetType.eq(targetType)
            .targetId.eq(targetId)
            .query().findPageDesc(pageNum, pageSize)
  }
}
