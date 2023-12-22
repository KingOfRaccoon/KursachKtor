package com.timetableGuap.network.response

import com.timetableGuap.database.data.DatabaseItem
import kotlinx.serialization.Serializable

@Serializable
data class PaginationData(
    val links: Links,
    val count: Int,
    val results: List<DatabaseItem>
)
