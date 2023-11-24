package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class Building(
    val id: Int,
    val name: String,
    val shortName: String
): DatabaseItem {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(20) to name,
            VarCharColumnType(10) to shortName
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "Building VALUES (?, ?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "name = EXCLUDED.name, shortname = EXCLUDED.shortname"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }
}
