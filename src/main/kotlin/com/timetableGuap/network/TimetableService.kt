package com.timetableGuap.network

import com.timetableGuap.network.data.*
import com.timetableGuap.util.Postman
import com.timetableGuap.util.Resource

class TimetableService(private val postman: Postman) {
    private val baseUrl = "https://test-rasp.guap.ru:9002"
    private val imagesUrl = "https://mobile.guap.ru/"
    private val buildingsListTag = "/api/building/list"
    private val typeLessonListTag = "/api/lesson/type/list"
    private val roomsListTag = "/api/room/list"
    private val groupsListTag = "/api/group/list"
    private val teachersListTag = "/api/teacher/list/"
    private val dayTimetableTag = "/api/rasp/day"
    private val weekTimetableTag = "/api/rasp/week"
    private val mouthTimetableTag = "/api/rasp/mouth"
    private val versionTag = "/api/db/version"
    private fun getTeacherImageTag(teacherId: Int) = "users/teacher/profile/$teacherId/pro/short"

    suspend fun getVersion(): Resource<Marker> {
        return postman.get(baseUrl, versionTag)
    }

    suspend fun getTeacherImages(teacherId: Int): Resource<ImagesTeacher> {
        return postman.get(imagesUrl, getTeacherImageTag(teacherId))
    }

    suspend fun getListBuildings(): Resource<TimetableBuildings> {
        return postman.get(baseUrl, buildingsListTag)
    }

    suspend fun getListTypeLessons(): Resource<TypesLessons> {
        return postman.get(baseUrl, typeLessonListTag)
    }

    suspend fun getListRooms(page: Int, pageSize: Int? = null): Resource<TimetableResponseData<Room>> {
        return postman.get(
            baseUrl,
            roomsListTag,
            arguments = mapOf("page" to page, "page_size" to pageSize).filter { it.value != null }
        )
    }

    suspend fun getListGroups(
        page: Int,
        pageSize: Int? = null
    ): Resource<TimetableResponseData<TimetableGroup>> {
        return postman.get(
            baseUrl,
            groupsListTag,
            arguments = mapOf("page" to page, "page_size" to pageSize).filter { it.value != null }
        )
    }

    suspend fun getListTeachers(
        page: Int,
        pageSize: Int? = null
    ): Resource<TimetableResponseData<TimetableTeacher>> {
        return postman.get(
            baseUrl,
            teachersListTag,
            arguments = mapOf("page" to page, "page_size" to pageSize).filter { it.value != null }
        )
    }

    suspend fun getDayTimetable(
        date: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        auditoryId: Int? = null
    ): Resource<Lessons> {
        return postman.get(
            baseUrl,
            "$dayTimetableTag/$date/",
            arguments = mapOf(
                "group_id" to groupId,
                "teacher_id" to teacherId,
                "auditory_id" to auditoryId
            ).filter { it.value != null }
        )
    }

    suspend fun getWeekTimetable(
        year: String,
        week: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        auditoryId: Int? = null
    ): Resource<Lessons> {
        return postman.get(
            baseUrl,
            "$weekTimetableTag/$year/$week",
            arguments = mapOf(
                "group_id" to groupId,
                "teacher_id" to teacherId,
                "auditory_id" to auditoryId
            ).filter { it.value != null }
        )
    }

    suspend fun getMouthTimetable(
        year: String,
        mouth: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        auditoryId: Int? = null
    ): Resource<Lessons> {
        return postman.get(
            baseUrl,
            "$mouthTimetableTag/$year/$mouth/",
            arguments = mapOf(
                "group_id" to groupId,
                "teacher_id" to teacherId,
                "auditory_id" to auditoryId
            ).filter { it.value != null }
        )
    }
}