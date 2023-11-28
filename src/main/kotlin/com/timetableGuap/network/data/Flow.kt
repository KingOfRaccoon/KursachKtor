package com.timetableGuap.network.data

import kotlinx.serialization.Serializable

@Serializable
data class Flow(
    val groups: List<Group>
)