package com.timetableGuap.database.data

import com.timetableGuap.network.data.TimetableBuilding
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

@Serializable
data class Building(
    val id: Int,
    val name: String,
    val shortName: String
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(20) to name,
            VarCharColumnType(10) to shortName
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "$nameTable VALUES (?, ?, ?)"
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

    companion object {
        const val nameTable = "Building"
        val convertToTimetableBuilding = { result: ResultSet ->
            TimetableBuilding(result.getInt(1), result.getString(2), result.getString(3))
        }
        val convertToBuilding = { resultSet: ResultSet ->
            Building(
                resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)
            )
        }
    }
}
