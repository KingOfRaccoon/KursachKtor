package com.timetableGuap.database.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ColumnType

@Serializable
abstract class DatabaseItem {
    abstract fun getColumnItems(): List<Pair<ColumnType, Any>>
    abstract fun getDatabaseTableNameWithPostfix(): String
    abstract fun getDatabaseUpdatePostfix(): String
    abstract fun getIdName(): String
    abstract fun needUpdate(): Boolean
}