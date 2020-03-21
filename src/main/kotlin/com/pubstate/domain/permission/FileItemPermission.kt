package com.pubstate.domain.permission

import com.pubstate.domain.entity.FileItem


class FileItemPermission(override val userId: Long, override val target: FileItem) : BasePermission() {
  override val targetId: Long = target.id

  override fun judge(): Boolean {
    return userId == target.ownerId
  }
}