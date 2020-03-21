package com.pubstate.vo

import com.pubstate.annotation.KotlinNoArg

@KotlinNoArg
data class UserInfo(
    var id: Long,
    var name: String,
    var avatar: String,
    var intro: String)
