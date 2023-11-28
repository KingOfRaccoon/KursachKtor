package com.timetableGuap.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    val disc: String,
    val duration: Double,
    @SerialName("type_id")
    val typeId: Int,
    val type: String = "",
    val typeShort: String = "",
    val color: String = "",
    val less: Int = 0
)