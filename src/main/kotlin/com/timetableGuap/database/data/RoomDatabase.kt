package com.timetableGuap.database.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

@Serializable
data class RoomDatabase(val buildingId: Int, val name: String) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to buildingId,
            VarCharColumnType(20) to name
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "$nameTable VALUES (?, ?)"
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

    companion object {
        const val nameTable = "Room"
        val convertToRoomDatabase = { result: ResultSet ->
            RoomDatabase(result.getInt(1), result.getString(2))
        }
    }
}