package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType

data class LessonTeacher(
    val teacherId: Int,
    val lessonId: Int
): DatabaseItem {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to teacherId,
            IntegerColumnType() to lessonId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "LessonTeacher VALUES (?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return ""
    }

    override fun getIdName(): String {
        return "teacherId,lessonId"
    }

    override fun needUpdate(): Boolean {
        return false
    }
}