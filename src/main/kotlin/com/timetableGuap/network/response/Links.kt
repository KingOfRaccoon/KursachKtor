package com.timetableGuap.network.response

import kotlinx.serialization.Serializable

@Serializable
data class Links(val previous: Boolean, val next: Boolean)
