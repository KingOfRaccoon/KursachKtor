package com.timetableGuap.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    @SerialName("group_id")
    val groupId: Int,
    val id: Int
)