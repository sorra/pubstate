package com.pubstate.domain.permission

import com.pubstate.domain.entity.Article


class ArticlePermission(
    override val userId: String,
    override val target: Article) : BasePermission() {

  override val targetId: String = target.id

  override fun judge(): Boolean {
    return userId == target.author.id
  }
}