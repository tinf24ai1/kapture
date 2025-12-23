package com.kapture.kapture.storage

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlin.random.Random

@Serializable
data class Item(
    val id: String = "${Clock.System.now().toEpochMilliseconds()}-${Random.nextInt()}",
    val title: String,
    val releaseDate: LocalDate,
    val idea: String,
    val notified: Boolean = false
)