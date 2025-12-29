package com.kapture.kapture.logger

// Object for logging messages (debug, error, info)
expect object Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String)
    fun i(tag: String, message: String)
}