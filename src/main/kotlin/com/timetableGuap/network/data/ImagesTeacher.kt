package com.timetableGuap.network.data

import kotlinx.serialization.Serializable

@Serializable
data class ImagesTeacher(
    val site_avatar_url: String = "",
    val thumb_file: String = ""
)