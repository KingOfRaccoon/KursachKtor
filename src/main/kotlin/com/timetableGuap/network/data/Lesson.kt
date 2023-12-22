package com.timetableGuap.network.data

import com.timetableGuap.network.PagingItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    @SerialName("dt_schedule")
    val dateTimeSchedule: String,
    val flow: Flow,
    val rooms: List<Room>,
    val subject: Subject,
    val teachers: List<Teacher>,
    val groups: List<TimetableGroup> = listOf(),
    val currentTeachers: List<TimetableTeacher> = listOf(),
    val less: Int = 0
): PagingItem