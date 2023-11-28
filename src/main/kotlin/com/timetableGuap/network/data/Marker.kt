package com.timetableGuap.network.data

import kotlinx.serialization.Serializable

/** Marker is data class for info about marker change data in server **/
@Serializable
data class Marker(
    var version: String
)