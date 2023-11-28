package com.timetableGuap.network.data

import kotlinx.serialization.Serializable

@Serializable
data class Lessons(
    var lessons: MutableList<Lesson>
)