package com.timetableGuap.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TypeLesson(
    val id: Int,
    val name: String,
    val type: String,
    @SerialName("normalized_color")
    val normalizedColor: String
)