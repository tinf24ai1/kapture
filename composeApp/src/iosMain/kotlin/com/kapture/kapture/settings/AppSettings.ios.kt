package com.kapture.kapture.settings

import platform.Foundation.NSUserDefaults

actual object AppSettings {
    private const val KEY_DENIAL_HINT = "denial_hint_shown"

    actual fun wasDenialHintShown(): Boolean =
        NSUserDefaults.standardUserDefaults.boolForKey(KEY_DENIAL_HINT)

    actual fun setDenialHintShown(shown: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(shown, forKey = KEY_DENIAL_HINT)
    }
}
