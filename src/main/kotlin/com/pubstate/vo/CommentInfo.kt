package com.pubstate.vo

import com.pubstate.annotation.KotlinNoArg
import com.pubstate.domain.enum.PubType
import java.util.*

@KotlinNoArg
data class CommentInfo(
    var id: String,
    var whenCreated: Date,
    var content: String,
    var author: UserBrief,
    var targetType: PubType,
    var targetId: String)
