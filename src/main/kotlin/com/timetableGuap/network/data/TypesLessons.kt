package com.timetableGuap.network.data

import kotlinx.serialization.Serializable

@Serializable
data class TypesLessons(
    val types: List<TypeLesson>
)