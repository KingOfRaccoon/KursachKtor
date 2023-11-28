package com.timetableGuap.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: Int,
    val tid: Int,
    @SerialName("user_id")
    val userId: Int?
)