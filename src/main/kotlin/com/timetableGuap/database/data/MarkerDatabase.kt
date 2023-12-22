package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

data class MarkerDatabase(val marker: String, val id: Int = 0): DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(256) to marker
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "$name VALUES (?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "marker = EXCLUDED.marker"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }

    companion object {
        const val name = "Marker"
        val convertToMarkerDatabase = { result: ResultSet ->
            MarkerDatabase(result.getString(2), result.getInt(1))
        }
    }
}