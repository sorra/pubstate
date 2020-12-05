package com.pubstate.domain.permission

import com.pubstate.exception.PermissionDeniedException

abstract class BasePermission {
  abstract val userId: String
  abstract val target: Any
  abstract val targetId: String

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