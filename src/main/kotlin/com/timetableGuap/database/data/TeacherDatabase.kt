package com.timetableGuap.database.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import java.sql.ResultSet

@Serializable
data class TeacherDatabase(
    val id: Int,
    val firstname: String,
    val secondName: String,
    val lastname: String,
    val tid: Int,
    val image: String,
    val imageThumb: String
) : DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            IntegerColumnType() to id,
            VarCharColumnType(50) to firstname,
            VarCharColumnType(50) to secondName,
            VarCharColumnType(50) to lastname,
            IntegerColumnType() to tid,
            VarCharColumnType(2048) to image,
            VarCharColumnType(2048) to imageThumb,
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "Teacher VALUES (?, ?, ?, ?, ?, ?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "firstname = EXCLUDED.firstname, secondname = EXCLUDED.secondName, lastname = EXCLUDED.lastname, " +
                "tid = EXCLUDED.tid, image = EXCLUDED.image, imageThumb = EXCLUDED.imageThumb"
    }

    override fun getIdName(): String {
        return "id"
    }

    override fun needUpdate(): Boolean {
        return true
    }

    companion object {
        const val nameTable = "Teacher"
        val convertToTeacherDatabase = { resultSet: ResultSet ->
            TeacherDatabase(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getInt(5),
                resultSet.getString(6),
                resultSet.getString(7),
            )
        }
    }
}
