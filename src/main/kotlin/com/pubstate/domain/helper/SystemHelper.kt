package com.pubstate.domain.helper

import com.pubstate.domain.entity.User
import com.pubstate.util.UniqueIdUtil

/**
 * https://stackoverflow.com/questions/12192050/what-are-the-differences-between-helper-and-utility-classes
 */
object SystemHelper {

  fun isInitialized() = User.byId(UniqueIdUtil.initial()) != null
}