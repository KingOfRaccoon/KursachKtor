package com.timetableGuap.database.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

@Serializable
class GroupDatabase(
    val id: Int,
    val groupName: String,
    val groupId: Int,
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(15) to groupName,
            IntegerColumnType() to groupId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return """$nameTable VALUES (?, ?, ?)"""
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

    companion object {
        const val nameTable = """"Group""""
        val convertToGroupDatabase = { result: ResultSet ->
            GroupDatabase(
                result.getInt(1),
                result.getString(2),
                result.getInt(3)
            )
        }
    }
}
