package com.metrowatch.kochi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform