package com.timetableGuap.error

import kotlinx.serialization.Serializable

@Serializable
data class RequestError(val error: String)