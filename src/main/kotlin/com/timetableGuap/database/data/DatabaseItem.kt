package com.timetableGuap.database.data

import org.jetbrains.exposed.sql.ColumnType

interface DatabaseItem {
    fun getColumnItems(): List<Pair<ColumnType, Any>>
    fun getDatabaseTableNameWithPostfix(): String
    fun getDatabaseUpdatePostfix(): String
    fun getIdName(): String
    fun needUpdate(): Boolean
}