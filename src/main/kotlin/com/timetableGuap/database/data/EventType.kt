package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType

data class EventType(
    val eventId: Int,
    val typeId: Int
) : DatabaseItem {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to eventId,
            IntegerColumnType() to typeId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "EventType VALUES (?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return ""
    }

    override fun getIdName(): String {
        return "eventId,typeId"
    }

    override fun needUpdate(): Boolean {
        return false
    }
}