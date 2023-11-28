package com.timetableGuap.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Room(
    @SerialName("building_id")
    val buildingId: Int,
    @SerialName("room_name")
    val name: String,
    var buildingName: String = ""
)