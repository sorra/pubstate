package com.pubstate.domain.helper

import com.pubstate.domain.entity.User
import com.pubstate.util.UniqueIdUtil

object AdminHelper {

  fun isAdmin(user: User?) = user?.id == UniqueIdUtil.initial()
}
