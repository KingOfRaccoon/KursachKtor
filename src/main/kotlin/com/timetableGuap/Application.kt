package com.timetableGuap

import com.timetableGuap.database.DatabaseFactory
import com.timetableGuap.network.TimetableService
import com.timetableGuap.network.TimetableViewModel
import com.timetableGuap.plugins.configureDatabases
import com.timetableGuap.plugins.configureHTTP
import com.timetableGuap.plugins.configureRouting
import com.timetableGuap.plugins.configureSerialization
import com.timetableGuap.time.DataTime
import com.timetableGuap.util.Postman
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.cors.routing.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val modules = module {
    single { Postman() }
    single { TimetableService(get()) }
    single { DatabaseFactory() }
    single { TimetableViewModel(get(), get()) }
}

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Get)
        HttpMethod.DefaultMethods.forEach {
            allowMethod(it)
        }
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeaders {
            true
        }
        allowOrigins {
            true
        }
        allowXHttpMethodOverride()
        allowHeader("key")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
    install(Koin) {
        modules(modules)
    }
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
