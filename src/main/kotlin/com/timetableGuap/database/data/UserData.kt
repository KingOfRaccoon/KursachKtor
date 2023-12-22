package com.timetableGuap.database.data

import com.timetableGuap.database.generateHash
import io.ktor.util.*
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

data class UserData(val login: String, val hexPassword: String, val userId: Int): DatabaseItem() {
    override fun getColumnItems(): List<Pair<ColumnType, Any>> {
        return listOf(
            VarCharColumnType(100) to login,
            TextColumnType() to generateHash(hexPassword, User::class.java.name),
            IntegerColumnType() to userId
        )
    }

    override fun getDatabaseTableNameWithPostfix(): String {
        return "$name VALUES (?, ?, ?)"
    }

    override fun getDatabaseUpdatePostfix(): String {
        return "hexPassword = EXCLUDED.hexPassword, userId = EXCLUDED.userId"
    }

    override fun getIdName(): String {
        return "login"
    }

    override fun needUpdate(): Boolean {
        return true
    }

    companion object {
        const val name = "UserData"
    }
}