package com.pubstate.domain.permission

import com.pubstate.domain.entity.Image

class ImagePermission(override val userId: String, override val target: Image) : BasePermission() {
  override val targetId: String = target.id

  override fun judge(): Boolean {
    return userId == target.owner.id
  }
}
