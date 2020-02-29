package com.pubstate.domain.entity

import java.util.*

fun Date?.orDefault() = this ?: Date(0)
