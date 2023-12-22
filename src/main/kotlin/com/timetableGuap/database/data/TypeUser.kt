package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType

data class TypeUser(
    val ownerId: Int,
    val typeId: Int
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to ownerId,
            IntegerColumnType() to typeId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return """"TypeUser" VALUES (?, ?)"""
    }

    override fun getDatabaseUpdatePostfix(): String {
        return ""
    }

    override fun getIdName(): String {
        return "ownerId,typeId"
    }

    override fun needUpdate(): Boolean {
        return false
    }
}