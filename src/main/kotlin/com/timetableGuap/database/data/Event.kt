package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class Event(
    val id: Int,
    val name: String,
    val place: String,
    val dateTimeStart: String,
    val dateTimeEnd: String
) : DatabaseItem {
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
        return """"Event" VALUES (?, ?, ?, ?, ?)"""
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
}