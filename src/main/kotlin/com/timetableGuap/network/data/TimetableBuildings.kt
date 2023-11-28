package com.timetableGuap.network.data

import kotlinx.serialization.Serializable

@Serializable
data class TimetableBuildings(
    val buildings: List<TimetableBuilding>
)