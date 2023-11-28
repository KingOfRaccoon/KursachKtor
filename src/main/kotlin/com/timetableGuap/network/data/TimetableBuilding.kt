package com.timetableGuap.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimetableBuilding(
    val id: Int = 0,
    val name: String = "",
    @SerialName("rasp_id")
    val raspId: String = ""
)