package com.timetableGuap.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimetableTeacher(
    @SerialName("academic_title")
    val academicTitle: Int?,
    val degree: Int?,
    @SerialName("explicit_pos_type_id")
    val explicitPositionTypeId: Int?,
    val firstname: String,
    val id: Int,
    val lastname: String,
    @SerialName("middlename")
    val middleName: String,
    val tid: Int,
    @SerialName("user_id")
    val userId: Int?,
    var image: String = "",
    var imageSite: String = ""
){
    fun fullName() = if (lastname.isNotEmpty() && middleName.isNotEmpty()) lastname + " " +
            (if (firstname.isNotEmpty()) firstname.first() + "." else "") +
            (if (middleName.isNotEmpty()) middleName.first() + "." else "")
    else
        firstname
}