package com.timetableGuap

import com.timetableGuap.database.DatabaseFactory
import com.timetableGuap.database.data.*
import com.timetableGuap.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    private val database = DatabaseFactory("test")
    private val list: List<DatabaseItem> = listOf(
        Building(0, "test", "t"),
        Event(1, "test", "test", "2023-11-15T09:30:00Z", "2023-11-15T11:30:00Z"),
        Group(3, "test", 0),
        Type(2, "fdf", "f", "", ""),
        Subject(6, "test", 90, 2),
        Lesson(4, 1, "2023-11-15T09:30:00Z", "groupId: 0", 6),
        Teacher(5, "te", "tet", "tewt", -1, "", ""),
        User(8, "ere", "dfs"),
        EventType(1, 2),
        Room(0, "11-11"),
        LessonGroup(3, 4),
        LessonRoom(0, "11-11", 4),
        LessonTeacher(5, 4),
        TypeUser(8, 2),
    )

    @Test
    fun testDatabase(){
        database.addItemsInDatabase(list)
    }
}
