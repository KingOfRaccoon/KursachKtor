package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class Type(
    val id: Int,
    val name: String,
    val shortName: String,
    val lightColor: String,
    val darkColor: String
) : DatabaseItem {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(100) to name,
            VarCharColumnType(20) to shortName,
            VarCharColumnType(7) to lightColor,
            VarCharColumnType(7) to darkColor
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return """"Type" VALUES (?, ?, ?, ?, ?)"""
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "name = EXCLUDED.name, shortName = EXCLUDED.shortName, " +
                "lightColor = EXCLUDED.lightColor, darkColor = EXCLUDED.darkColor"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }
}