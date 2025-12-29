package com.kapture.kapture.settings

// Object for saving Answer of settings dialog
expect object AppSettings {
    fun wasDenialHintShown(): Boolean
    fun setDenialHintShown(shown: Boolean = true)
}
