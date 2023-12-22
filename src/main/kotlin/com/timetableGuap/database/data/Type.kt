package com.timetableGuap.database.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

@Serializable
data class Type(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val shortName: String,
    val lightColor: String,
    val darkColor: String
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            IntegerColumnType() to ownerId,
            VarCharColumnType(100) to name,
            VarCharColumnType(20) to shortName,
            VarCharColumnType(7) to lightColor,
            VarCharColumnType(7) to darkColor
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return """$nameTable VALUES (?, ?, ?, ?, ?, ?)"""
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "name = EXCLUDED.name, shortName = EXCLUDED.shortName, " +
                "lightColor = EXCLUDED.lightColor, darkColor = EXCLUDED.darkColor"
    }

    override fun getIdName(): String {
        return nameId
    }

    override fun needUpdate(): Boolean {
        return true
    }

    companion object {
        const val nameTable = """"Type""""
        const val nameId = "id"
        val convertToType = { resultSet: ResultSet ->
            Type(
                resultSet.getInt(1),
                resultSet.getInt(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
            )
        }
    }
}