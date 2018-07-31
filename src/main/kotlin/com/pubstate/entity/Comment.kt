package com.pubstate.entity

import com.avaje.ebean.PagedList
import com.avaje.ebean.annotation.SoftDelete
import com.pubstate.dto.CommentInfo
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

  companion object : BaseFind<Long, Comment>() {
    fun commentsOf(targetType: PubType, targetId: Long,
                   pageNum: Int, pageSize: Int): PagedList<Comment> =
        where().eq("targetType", targetType.ordinal).eq("targetId", targetId)
            .findPageDesc(pageNum, pageSize)
  }
}
