package com.pubstate.domain.permission

import com.pubstate.domain.i18n.MessageBundle
import com.pubstate.exception.PermissionDeniedException

abstract class BasePermission {
  abstract val userId: String
  abstract val target: Any
  abstract val targetId: String

  protected abstract fun judge(): Boolean

  fun canEdit() {
    can(judge(), "action_edit")
  }

  fun canDelete() {
    can(judge(), "action_delete")
  }

  private fun can(checkResult: Boolean, actionCode: String) {
    if (!checkResult) {
      val action = MessageBundle.getMessage(actionCode)
      val target = "${target.javaClass.simpleName}[$targetId]"
      val message = MessageBundle.getMessage("permission_denied", arrayOf(userId, action, target))
      throw PermissionDeniedException(message)
    }
  }
}
