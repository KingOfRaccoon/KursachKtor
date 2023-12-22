package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class SubjectDatabase(
    val id: Int,
    val name: String,
    val duration: Int,
    val typeId: Int
): DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(100) to name,
            IntegerColumnType() to duration,
            IntegerColumnType() to typeId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "Subject VALUES (?, ?, ?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "name = EXCLUDED.name, duration = EXCLUDED.duration, typeId = EXCLUDED.typeId"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }
}