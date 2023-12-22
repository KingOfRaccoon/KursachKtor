package com.timetableGuap.network.response

import kotlinx.serialization.Serializable

@Serializable
data class EventLessonResponse(
    val lessons: List<LessonResponse>,
    val events: List<EventResponse>
)