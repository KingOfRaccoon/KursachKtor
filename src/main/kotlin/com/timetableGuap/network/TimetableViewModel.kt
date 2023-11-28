package com.timetableGuap.network

import com.timetableGuap.database.DatabaseFactory
import com.timetableGuap.database.data.*
import com.timetableGuap.network.data.*
import com.timetableGuap.network.data.Marker
import com.timetableGuap.time.DataTime
import com.timetableGuap.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class TimetableViewModel(private val timetableService: TimetableService, private val databaseFactory: DatabaseFactory) {
    private val _teachersStateFlow =
        MutableStateFlow<Resource<TimetableResponseData<TimetableTeacher>>>(Resource.Loading())
    val teachersStateFlow = _teachersStateFlow.asStateFlow()

    private val _groupsStateFlow =
        MutableStateFlow<Resource<TimetableResponseData<TimetableGroup>>>(Resource.Loading())
    val groupsStateFlow = _groupsStateFlow.asStateFlow()

    private val _teachersImagesStateFlow = MutableStateFlow(ImagesTeachers())
    val teachersImagesStateFlow = _teachersImagesStateFlow.asStateFlow()

    private val _buildingsStateFlow =
        MutableStateFlow<Resource<TimetableBuildings>>(Resource.Loading())
    val buildingsStateFlow = _buildingsStateFlow.asStateFlow()

    private val _typesLessonsStateFlow =
        MutableStateFlow<Resource<TypesLessons>>(Resource.Loading())
    val typesLessonsStateFlow = _typesLessonsStateFlow.asStateFlow()

    private val _timetableFlow = MutableStateFlow(Timetable(mutableMapOf()))
    val timetableFlow = _timetableFlow.asStateFlow()

    private val _markerFlow = MutableStateFlow<Resource<Marker>>(Resource.Loading())
    val markerFlow = _markerFlow.asStateFlow()

    private val teachersScope = CoroutineScope(Dispatchers.IO)
    private val groupsScope = CoroutineScope(Dispatchers.IO)
    private val timetableScope = CoroutineScope(Dispatchers.IO)

    init {
        loadMarker()
    }

    fun updateDatabase() {
        println("updateDatabase")
        loadTeachers()
        loadGroups()
        loadTypesLessons()
        loadBuildings()
    }

    private fun loadMarker() {
        timetableScope.launch {
            val oldMarker =
                databaseFactory.getAllItemsFromDataBase(MarkerDatabase.name, MarkerDatabase.convertToMarkerDatabase)
            if (oldMarker.isEmpty()) {
                _markerFlow.emit(timetableService.getVersion().also {
                    if (it is Resource.Success)
                        databaseFactory.addItemInDatabase(MarkerDatabase(it.data.version), true)
                })
                updateDatabase()
            } else {
                if (timetableService.getVersion().data?.version != oldMarker.first().marker){
                    _markerFlow.emit(timetableService.getVersion().also {
                        if (it is Resource.Success)
                            databaseFactory.addItemInDatabase(MarkerDatabase(it.data.version), true)
                    })
                    updateDatabase()
                }
            }
        }
    }

    private fun loadTeachers() {
        teachersScope.launch {
            val list = Resource.Loading(TimetableResponseData<TimetableTeacher>())
            var page = 1
            var http: Resource<TimetableResponseData<TimetableTeacher>>
            do {
                http = timetableService.getListTeachers(page)
                if (http is Resource.Success) {
                    list.data = list.data?.plus((http.data))
                }
                page++
                _teachersStateFlow.update {
                    if (list.data?.links?.next == true)
                        list.copy(list.data?.copy(id = Random.nextInt()))
                    else
                        Resource.Success(list.data?.copy(id = Random.nextInt()) ?: TimetableResponseData()).also {
                            databaseFactory.addItemsInDatabase(it.data.results.map {
                                TeacherDatabase(
                                    it.id,
                                    it.firstname,
                                    it.middleName,
                                    it.lastname,
                                    it.tid,
                                    it.image,
                                    it.imageSite
                                )
                            })
                        }
                }
            } while (list.data?.links?.next == true)
        }
    }

    private fun loadGroups() {
        groupsScope.launch {
            val list = Resource.Loading(TimetableResponseData<TimetableGroup>())
            var page = 1
            var http: Resource<TimetableResponseData<TimetableGroup>>
            do {
                http = timetableService.getListGroups(page)
                if (http is Resource.Success)
                    list.data = list.data?.plus((http.data))
                page++
                _groupsStateFlow.update {
                    if (list.data?.links?.next == true)
                        list.copy(list.data?.copy(id = Random.nextInt()))
                    else
                        Resource.Success(list.data?.copy(id = Random.nextInt()) ?: TimetableResponseData())
                            .also {
                                databaseFactory.addItemsInDatabase(it.data.results.map {
                                    GroupDatabase(
                                        it.id,
                                        it.groupName,
                                        it.groupId
                                    )
                                })
                            }
                }
            } while (list.data?.links?.next == true)
        }
    }

    fun loadImagesTeachers(teacherIds: List<Int>) {
        timetableScope.launch {
            loadImagesTeachersSuspend(teacherIds)
        }
    }

    private suspend fun loadImagesTeachersSuspend(teacherIds: List<Int>) {
        withContext(Dispatchers.IO) {
            _teachersImagesStateFlow.update {
                _teachersImagesStateFlow.value.addImages(
                    teacherIds.filter { _teachersImagesStateFlow.value.imagesTeachers[it]?.thumb_file == null }
                        .map {
                            async {
                                it to (timetableService.getTeacherImages(
                                    it
                                ).data ?: ImagesTeacher())
                            }
                        }.awaitAll().toMap()
                )
            }
        }
    }

    private fun loadTypesLessons() {
        timetableScope.launch {
            _typesLessonsStateFlow.emit(timetableService.getListTypeLessons().also {
                if (it is Resource.Success)
                    it.data.types.map {
                        Type(
                            it.id,
                            it.name,
                            it.type,
                            "",
                            it.normalizedColor
                        )
                    }
            })
        }
    }

    private fun loadBuildings() {
        timetableScope.launch {
            _buildingsStateFlow.emit(timetableService.getListBuildings())
        }
    }

    fun loadWeekTimetable(
        year: String,
        week: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null
    ) {
        val searchName = generateTag(groupId, teacherId, roomId)
        timetableScope.launch {
            val data = convertWeekToDays(
                timetableService.getWeekTimetable(year, week, groupId, teacherId, roomId)
            )
            _timetableFlow.update {
                _timetableFlow.value.mergeMaps(
                    searchName, data
                )
            }
        }
    }

    fun loadDayTimetable(
        day: String,
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null
    ) {
        timetableScope.launch {
            val searchName = generateTag(groupId, teacherId, roomId)
            if (timetableFlow.value.timetable[searchName]?.containsKey(day) != true
                || timetableFlow.value.timetable[searchName]?.get(day) !is Resource.Success
            ) {
                val data = timetableService.getDayTimetable(day, groupId, teacherId, roomId)

                _timetableFlow.update {
                    _timetableFlow.value.mergeMaps(
                        searchName, mapOf(
                            day to data
                        )
                    )
                }
            }
        }
    }

    fun generateTag(
        groupId: Int? = null,
        teacherId: Int? = null,
        roomId: Int? = null
    ): String {
        var tag = ""
        if (groupId != null)
            tag += "groupId: $groupId"

        if (teacherId != null)
            tag += "teacherId: $teacherId"

        if (roomId != null)
            tag += "roomId: $roomId"

        return tag
    }

    private fun convertWeekToDays(
        week: Resource<Lessons>,
        dataTime: DataTime = DataTime.getStartThisWeek()
    ): MutableMap<String, Resource<Lessons>> {
        val mapDays = mutableMapOf<String, Resource<Lessons>>()
        when (week) {
            is Resource.Error -> mapDays.putAll(
                List(7) { dataTime.goToNNextDay(it).getIsoFormat() to week }
            )

            is Resource.Loading -> mapDays.putAll(
                List(7) { dataTime.goToNNextDay(it).getIsoFormat() to week }
            )

            is Resource.Success -> mapDays.putAll(
                week.data.lessons.groupBy { it.dateTimeSchedule.split("T").first() }
                    .mapValues { Resource.Success(Lessons(it.value.toMutableList())) }.toList()
                    .toMutableList()
                    .sortedBy { it.first }
            )
        }

        return addDaysWithoutLessons(mapDays)
    }

    private fun addDaysWithoutLessons(dayLessons: MutableMap<String, Resource<Lessons>>): MutableMap<String, Resource<Lessons>> {
        val lessons = dayLessons.toList().toMutableList()
        var sizeNeed = 7 - lessons.size
        var i = 0
        if (lessons.isNotEmpty()) {
            if (DataTime.parseFromTimeTable(lessons.minByOrNull { it.first }!!.first).dayOfWeek != 1) {
                var startWeek =
                    DataTime.parseFromTimeTable(lessons.minByOrNull { it.first }!!.first)
                startWeek = startWeek.goToNNextDay(-(startWeek.dayOfWeek - 1))
                repeat(DataTime.parseFromTimeTable(lessons.minByOrNull { it.first }!!.first).dayOfWeek - 1) {
                    lessons.add(
                        it, startWeek.goToNNextDay(it)
                            .getIsoFormat() to Resource.Success(Lessons(mutableListOf()))
                    )
                    sizeNeed--
                }
            }
        } else {
            val startWeek = DataTime.getStartThisWeek()
            return List(7) { startWeek.goToNNextDay(it) }.associate {
                it.getIsoFormat() to Resource.Success(Lessons(mutableListOf()))
            }.toMutableMap()
        }

        while (sizeNeed > 0 && i < lessons.lastIndex) {
            if (DataTime.parseFromTimeTable(lessons[i].first).tomorrow()
                    .getIsoFormat() != lessons[i + 1].first
            ) {
                lessons.add(
                    i + 1, DataTime.parseFromTimeTable(lessons[i].first).tomorrow()
                        .getIsoFormat() to Resource.Success(Lessons(mutableListOf()))
                )
                sizeNeed--
            }

            i++
        }
        if (lessons.isNotEmpty()) {
            for (k in 0 until sizeNeed)
                lessons.add(
                    DataTime.parseFromTimeTable(lessons.last().first).tomorrow()
                        .getIsoFormat() to Resource.Success(Lessons(mutableListOf()))
                )
        }
        return lessons.toMap().toMutableMap()
    }

    fun convertLessonApiToDatabase(
        lesson: Lesson,
        idLesson: Int,
        filter: String
    ): Triple<LessonDatabase, SubjectDatabase, Triple<List<LessonGroup>, List<LessonRoom>, List<LessonTeacher>>> {
        return Triple(
            LessonDatabase(
                idLesson,
                lesson.less,
                lesson.dateTimeSchedule,
                filter,
                generateIdSubject(lesson.subject.disc, lesson.subject.typeId)
            ),
            SubjectDatabase(
                generateIdSubject(lesson.subject.disc, lesson.subject.typeId),
                lesson.subject.disc,
                (lesson.subject.duration * 90).toInt(),
                lesson.subject.typeId
            ),
            Triple(
                lesson.flow.groups.map { LessonGroup(it.id, idLesson) },
                lesson.rooms.map { LessonRoom(it.buildingId, it.name, idLesson) },
                lesson.teachers.map { LessonTeacher(it.id, idLesson) }
            )
        )
    }

    private fun generateIdSubject(name: String, typeId: Int) = SubjectForGenerateId(name, typeId).hashCode()

    inner class SubjectForGenerateId(name: String, typeId: Int)
}