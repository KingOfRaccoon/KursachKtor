package com.timetableGuap.database.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

@Serializable
data class LessonDatabase(
    val id: Int,
    val number: Int,
    val dateTimeStart: String,
    val filterTimetable: String,
    val subjectId: Int
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            IntegerColumnType() to number,
            VarCharColumnType(30) to dateTimeStart,
            VarCharColumnType(30) to filterTimetable,
            IntegerColumnType() to subjectId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "$nameTable VALUES (?, ?, ?, ?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "number = EXCLUDED.number, dateTimeStart = EXCLUDED.dateTimeStart, " +
                "filterTimetable = EXCLUDED.filterTimetable, subjectId = EXCLUDED.subjectId"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }

    companion object {
        const val nameTable = "Lesson"
    }
}