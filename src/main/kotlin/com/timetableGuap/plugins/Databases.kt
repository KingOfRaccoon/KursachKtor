package com.timetableGuap.plugins

import com.timetableGuap.network.TimetableViewModel
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureDatabases() {
    val viewModel: TimetableViewModel by inject()
    routing {

    }
}