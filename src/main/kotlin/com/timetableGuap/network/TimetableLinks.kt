package com.timetableGuap.network

import kotlinx.serialization.Serializable

/** Links is data class for info about links page pagination **/
@Serializable
data class TimetableLinks(
    var next: Boolean = false,
    var previous: Boolean = false
)