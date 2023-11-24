package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class Group(
    val id: Int,
    val groupName: String,
    val groupId: Int,
) : DatabaseItem {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(15) to groupName,
            IntegerColumnType() to groupId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return """"Group" VALUES (?, ?, ?)"""
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "groupName = EXCLUDED.groupName, groupId = EXCLUDED.groupId"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }
}
