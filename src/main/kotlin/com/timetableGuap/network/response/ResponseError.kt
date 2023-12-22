package com.timetableGuap.network.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(val error: String)