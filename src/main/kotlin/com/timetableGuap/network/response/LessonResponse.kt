package com.timetableGuap.network.response

import com.timetableGuap.database.data.RoomDatabase
import com.timetableGuap.network.data.Room
import com.timetableGuap.util.arrayToListInt
import com.timetableGuap.util.arrayToListRooms
import kotlinx.serialization.Serializable
import java.sql.ResultSet

@Serializable
data class LessonResponse(
    val id: Int,
    val number: Int,
    val dateTimeStart: String,
    val filter: String,
    val subjectId: Int,
    val nameSubject: String,
    val duration: Int,
    val typeId: Int,
    val teachersIds: List<Int> = listOf(),
    val groupsIds: List<Int> = listOf(),
    val rooms: List<RoomDatabase> = listOf(),
) {
    companion object {
        val convertToLessonResponse = { result: ResultSet ->
            LessonResponse(
                result.getInt(1),
                result.getInt(2),
                result.getString(3),
                result.getString(4),
                result.getInt(5),
                result.getString(9),
                result.getInt(10),
                result.getInt(11),
                arrayToListInt(result.getArray(6).resultSet),
                arrayToListInt(result.getArray(7).resultSet),
                arrayToListRooms(result.getArray(8).resultSet)
            )
        }
    }
}