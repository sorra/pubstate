package com.pubstate.domain.helper

import com.pubstate.domain.entity.User
import com.pubstate.util.UniqueIdUtil

object AdminHelper {

  fun isAdmin(userId: String?) = userId == UniqueIdUtil.initial()

  fun isAdmin(user: User?) = isAdmin(user?.id)
}
