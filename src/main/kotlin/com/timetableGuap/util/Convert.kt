package com.timetableGuap.util

import com.timetableGuap.database.data.RoomDatabase
import java.sql.ResultSet

fun arrayToListInt(array: ResultSet): MutableList<Int> {
    val mutableList = mutableListOf<Int>()
    while (array.next()) {
        mutableList.add(array.getInt(2))
    }
    return mutableList
}

fun arrayToListRooms(array: ResultSet): MutableList<RoomDatabase> {
    val mutableList = mutableListOf<RoomDatabase>()
    while (array.next()) {
        mutableList.add(array.getString(2).split(":").let { RoomDatabase(it.first().toInt(), it.last()) })
    }
    return mutableList
}