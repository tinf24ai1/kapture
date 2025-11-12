package com.kapture.kapture.storage

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class Item(
    val title: String,
    val releaseDate: LocalDate, //Important
    val idea: String
)