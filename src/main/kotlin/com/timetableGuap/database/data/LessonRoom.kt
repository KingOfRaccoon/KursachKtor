package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class LessonRoom(
    val buildingId: Int,
    val name: String,
    val lessonId: Int
): DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to buildingId,
            VarCharColumnType(20) to name,
            IntegerColumnType() to lessonId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "LessonRoom VALUES (?, ?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return ""
    }

    override fun getIdName(): String {
        return "buildingId,name,lessonId"
    }

    override fun needUpdate(): Boolean {
        return false
    }
}