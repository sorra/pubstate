package com.pubstate.entity

import java.util.*

fun Date?.orDefault() = this ?: Date(0)
