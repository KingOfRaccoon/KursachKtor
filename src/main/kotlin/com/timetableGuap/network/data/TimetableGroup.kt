package com.timetableGuap.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimetableGroup(
    @SerialName("cur_grade")
    val currentGrade: Int,
    @SerialName("group_id")
    val groupId: Int,
    @SerialName("group_name")
    val groupName: String,
    val id: Int
)