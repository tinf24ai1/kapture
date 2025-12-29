package com.kapture.kapture.storage

import com.kapture.kapture.logger.Logger
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.russhwolf.settings.Settings

object LocalStorage {

    private val settings = Settings()

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    internal inline fun <reified T> save(key: String, value: T) {
        try {
            val encoded = json.encodeToString(value)
            settings.putString(key, encoded)
            Logger.d("LocalStorage", "Saved key='$key'")
        } catch (e: Exception){
            Logger.e("LocalStorage", "Error saving key='$key': ${e.message}")
        }
    }

    internal inline fun <reified T> restore(key: String): T? {
        val encoded = settings.getStringOrNull(key)

        if (encoded == null) {
            Logger.i("LocalStorage", "No data found for key='$key'")
            return null
        }

        return try {
            val decoded = json.decodeFromString<T>(encoded)
            Logger.d("LocalStorage", "Restored key='$key' (type=${T::class.simpleName})")
            decoded
        } catch (e: Exception){
            Logger.e("LocalStorage", "Error decoding key='$key': ${e.message}")
            null
        }
    }

    fun isset(key: String): Boolean = settings.hasKey(key)
}