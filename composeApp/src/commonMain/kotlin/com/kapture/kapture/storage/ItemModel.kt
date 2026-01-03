package com.kapture.kapture.storage

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlin.random.Random

// Data Model for storing an Idea-Item in Capsule
@Serializable
data class ItemModel(
    val id: String = "${Clock.System.now().toEpochMilliseconds()}-${Random.nextInt()}",
    val title: String,
    val idea: String,
    val releaseDate: LocalDate,
    val notified: Boolean = false,
    val startDate: Int,
    val endDate: Int
)