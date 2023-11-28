package com.timetableGuap.network.data

import com.timetableGuap.util.Resource
import kotlin.random.Random

data class Timetable(
    val timetable: MutableMap<String, Map<String, Resource<Lessons>>> = mutableMapOf(),
    private val id: Int = Random.nextInt()
) {
    fun mergeMaps(
        key: String,
        miniMap: Map<String, Resource<Lessons>>
    ): Timetable {
        return Timetable(timetable = timetable.apply {
            timetable.put(key, (timetable[key].orEmpty().keys + miniMap.keys).associateWith {
                val bidData = timetable[key]?.get(it)
                val miniData = miniMap[it]
                if (bidData != null && miniData != null) {
                    if (bidData is Resource.Success && miniData is Resource.Success) {
                        miniData.apply {
                            this.data.lessons = (this.data.lessons + bidData.data.lessons)
                                .distinctBy { it.dateTimeSchedule }
                                .toMutableList()
                        }.also { println("update data: ${it.data.lessons.size}") }
                    } else if (bidData is Resource.Success && miniData is Resource.Error) {
                        bidData
                    } else if (bidData is Resource.Error && miniData is Resource.Success) {
                        miniData
                    } else {
                        Resource.Loading()
                    }
                } else if (bidData != null && miniData == null) {
                    bidData
                } else if (bidData == null && miniData != null) {
                    miniData
                } else
                    Resource.Loading()
            })?.toMutableMap()
        })
    }
}