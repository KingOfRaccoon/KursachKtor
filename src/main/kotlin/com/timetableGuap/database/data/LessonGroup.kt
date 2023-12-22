package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType

data class LessonGroup(
    val groupId: Int,
    val lessonId: Int
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to groupId,
            IntegerColumnType() to lessonId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "LessonGroup VALUES (?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return ""
    }

    override fun getIdName(): String {
        return "groupId,lessonId"
    }

    override fun needUpdate(): Boolean {
        return false
    }
}