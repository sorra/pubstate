package com.pubstate.vo

import com.pubstate.annotation.KotlinNoArg

@KotlinNoArg
data class UserSelf(
    var id: String,
    var name: String,
    var avatar: String)
