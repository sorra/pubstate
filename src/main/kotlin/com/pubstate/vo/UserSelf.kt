package com.pubstate.vo

import com.pubstate.annotation.KotlinNoArg

@KotlinNoArg
data class UserSelf(
    var id: Long,
    var name: String,
    var avatar: String)
