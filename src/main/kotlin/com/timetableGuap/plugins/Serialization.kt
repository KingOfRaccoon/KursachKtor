package com.timetableGuap.plugins

import com.timetableGuap.database.data.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        serialization(ContentType.Application.Json, Json {
            this.serializersModule = SerializersModule {
                this.polymorphic(DatabaseItem::class) {
                    subclass(GroupDatabase::class, GroupDatabase.serializer())
                    subclass(TeacherDatabase::class, TeacherDatabase.serializer())
                    subclass(RoomDatabase::class, RoomDatabase.serializer())
                    subclass(Type::class, Type.serializer())
                    subclass(Building::class, Building.serializer())
                }
            }
        })
    }
}
