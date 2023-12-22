package com.timetableGuap.network.response

import com.timetableGuap.util.arrayToListInt
import kotlinx.serialization.Serializable
import java.sql.ResultSet

@Serializable
data class EventResponse(
    val id: Int,
    val name: String,
    val place: String,
    val dateTimeStart: String,
    val dateTimeEnd: String,
    val typeIds: List<Int>
) {
    companion object {
        val convertToEventResponse = { result: ResultSet ->
            EventResponse(
                result.getInt(1),
                result.getString(2),
                result.getString(3),
                result.getString(4),
                result.getString(5),
                arrayToListInt(result.getArray(6).resultSet),
            )
        }
    }
}