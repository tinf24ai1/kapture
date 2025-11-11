package com.kapture.kapture.settings

expect object AppSettings {
    fun wasDenialHintShown(): Boolean
    fun setDenialHintShown(shown: Boolean = true)
}