package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class Teacher(
    val id: Int,
    val firstname: String,
    val secondName: String,
    val lastname: String,
    val tid: Int,
    val image: String,
    val imageThumb: String
) : DatabaseItem {
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
}
