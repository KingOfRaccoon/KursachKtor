package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class Room(val buildingId: Int, val name: String) : DatabaseItem {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to buildingId,
            VarCharColumnType(20) to name
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "Room VALUES (?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return ""
    }

    override fun getIdName(): String {
        return "buildingId,name"
    }

    override fun needUpdate(): Boolean {
        return false
    }
}