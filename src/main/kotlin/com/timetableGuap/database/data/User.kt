package com.timetableGuap.database.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

@Serializable
data class User(
    val id: Int,
    val firstName: String,
    val secondName: String,
    val filter: String
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(60) to firstName,
            VarCharColumnType(60) to secondName,
            VarCharColumnType(60) to filter
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return """"$name" VALUES (?, ?, ?, ?)"""
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

    companion object {
        const val name = "User"
        val convertToUser = { result: ResultSet ->
            User(result.getInt(1), result.getString(2), result.getString(3), result.getString(4))
        }
    }
}