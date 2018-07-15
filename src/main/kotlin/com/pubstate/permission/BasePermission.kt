package com.pubstate.permission

import com.pubstate.exception.PermissionDeniedException

abstract class BasePermission {
  abstract val userId: Long
  abstract val target: Any
  abstract val targetId: Long

  protected abstract fun judge(): Boolean

  fun canEdit() {
    can(judge(), "edit")
  }

  fun canDelete() {
    can(judge(), "delete")
  }

  private fun can(checkResult: Boolean, action: String) {
    if (!checkResult) {
      throw PermissionDeniedException("User[$userId] is not permitted to $action ${target.javaClass.simpleName}[$targetId]")
    }
  }
}