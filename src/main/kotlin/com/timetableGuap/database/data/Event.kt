package com.timetableGuap.database.data

import com.timetableGuap.network.response.EventResponse
import com.timetableGuap.network.response.LessonResponse
import com.timetableGuap.util.arrayToListInt
import com.timetableGuap.util.arrayToListRooms
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

@Serializable
data class Event(
    val id: Int,
    val name: String,
    val place: String,
    val dateTimeStart: String,
    val dateTimeEnd: String
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(60) to name,
            TextColumnType() to place,
            VarCharColumnType(30) to dateTimeStart,
            VarCharColumnType(30) to dateTimeEnd
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return """$nameTable VALUES (?, ?, ?, ?, ?)"""
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "name = EXCLUDED.name, place = EXCLUDED.place, " +
                "dateTimeStart = EXCLUDED.dateTimeStart, dateTimeEnd = EXCLUDED.dateTimeEnd"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }

    companion object {
        const val nameTable = """"Event""""
    }
}