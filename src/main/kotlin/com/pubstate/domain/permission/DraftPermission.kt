package com.pubstate.domain.permission

import com.pubstate.domain.entity.Draft

class DraftPermission(
    override val userId: Long,
    override val target: Draft) : BasePermission() {

  override val targetId: Long = target.id

  override fun judge(): Boolean {
    return userId == target.author.id
  }
}
