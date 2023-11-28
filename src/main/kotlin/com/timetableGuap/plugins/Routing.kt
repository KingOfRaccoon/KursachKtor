package com.timetableGuap.plugins

import com.timetableGuap.error.RequestError
import com.timetableGuap.network.TimetableService
import com.timetableGuap.network.TimetableViewModel
import com.timetableGuap.util.Postman
import com.timetableGuap.util.Resource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.collectLatest
import org.apache.commons.validator.GenericValidator
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val viewModel: TimetableViewModel by inject()
    routing {
        get("/") {
            call.respondText(TimetableService(Postman()).getVersion().toString())
        }

        get("/dayTimetable/{${Tag.Date.tag}}") {
//            if (call.parameters["date"] == null)
//                call.respond(HttpStatusCode.UnprocessableEntity, RequestError("date is not found"))

            if (!GenericValidator.isDate(call.parameters[Tag.Date.tag].orEmpty(), "yyyy-MM-dd", true))
                call.respond(HttpStatusCode.UnprocessableEntity, RequestError("date is not correct"))

            if (call.parameters[Tag.GroupId.tag] == null && call.parameters[Tag.TeacherId.tag] == null
                && call.parameters[Tag.RoomId.tag] == null
            )
                call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    RequestError("At least 1 query (${Tag.GroupId.tag} or ${Tag.TeacherId.tag} " +
                            "or ${Tag.RoomId.tag}) parameter required")
                )

            viewModel.loadDayTimetable(
                call.parameters[Tag.Date.tag].orEmpty(),
                call.parameters[Tag.GroupId.tag]?.toInt(),
                call.parameters[Tag.TeacherId.tag]?.toInt(),
                call.parameters[Tag.RoomId.tag]?.toInt()
            )

            viewModel.timetableFlow.collectLatest {
                it.timetable[viewModel.generateTag(call.parameters[Tag.GroupId.tag]?.toInt(),
                    call.parameters[Tag.TeacherId.tag]?.toInt(),
                    call.parameters[Tag.RoomId.tag]?.toInt())]?.get(call.parameters[Tag.Date.tag].orEmpty())?.let {
                    if (it is Resource.Success)
                        call.respondText(it.toString())
                }
            }
        }
    }
}

enum class Tag(val tag: String) {
    Date("date"),
    GroupId("groupId"),
    TeacherId("teacherId"),
    RoomId("roomId")
}