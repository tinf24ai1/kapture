package com.kapture.kapture.settings

import android.content.Context

actual object AppSettings {
    private const val PREF = "kapture_prefs"
    private const val KEY_DENIAL_HINT = "denial_hint_shown"

    private val prefs by lazy {
        AndroidContextHolder.appContext.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    actual fun wasDenialHintShown(): Boolean =
        prefs.getBoolean(KEY_DENIAL_HINT, false)

    actual fun setDenialHintShown(shown: Boolean) {
        prefs.edit().putBoolean(KEY_DENIAL_HINT, shown).apply()
    }
}
