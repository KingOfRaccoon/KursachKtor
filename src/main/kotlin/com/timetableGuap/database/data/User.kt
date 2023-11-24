package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class User(
    val id: Int,
    val firstName: String,
    val secondName: String
) : DatabaseItem {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(60) to firstName,
            VarCharColumnType(60) to secondName
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return """"User" VALUES (?, ?, ?)"""
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "firstName = EXCLUDED.firstName, secondName = EXCLUDED.secondName"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }
}