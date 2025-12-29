package com.kapture.kapture.logger

import android.util.Log

// Actual implementation of Logger for Android platform
actual object Logger {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
    actual fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
    actual fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
}