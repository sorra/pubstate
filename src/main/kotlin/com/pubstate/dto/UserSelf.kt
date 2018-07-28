package com.pubstate.dto

import com.pubstate.annotation.KotlinNoArg

@KotlinNoArg
data class UserSelf(val id: Long, val name: String, val avatar: String)
