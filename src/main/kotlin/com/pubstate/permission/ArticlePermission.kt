package com.pubstate.permission

import com.pubstate.entity.Article


class ArticlePermission(
    override val userId: Long,
    override val target: Article) : BasePermission() {

  override val targetId: Long = target.id

  override fun judge(): Boolean {
    return userId == target.author.id
  }
}