package com.kapture.kapture.storage

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Item(
    val title: String,
    val releaseDate: Instant, //Important
    val idea: String
)
