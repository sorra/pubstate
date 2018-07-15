package com.pubstate.dto

import com.pubstate.annotation.KotlinNoArg
import com.pubstate.entity.PubType
import java.util.*

@KotlinNoArg
data class CommentInfo(
    var id: Long,
    var whenCreated: Date,
    var content: String,
    var author: UserBrief,
    var targetType: PubType,
    var targetId: Long)
