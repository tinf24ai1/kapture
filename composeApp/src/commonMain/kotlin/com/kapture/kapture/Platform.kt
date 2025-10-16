package com.kapture.kapture

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform