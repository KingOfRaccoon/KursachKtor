package com.timetableGuap.network.data

import kotlin.random.Random


data class ImagesTeachers(
    val imagesTeachers: MutableMap<Int, ImagesTeacher> = mutableMapOf(),
    private val id: Int = Random.nextInt()
) {
    fun addImages(newImagesTeachers: Map<Int, ImagesTeacher>): ImagesTeachers {
        imagesTeachers.putAll(newImagesTeachers)

        return ImagesTeachers(imagesTeachers)
    }
}