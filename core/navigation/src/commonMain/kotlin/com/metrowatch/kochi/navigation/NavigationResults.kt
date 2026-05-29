package com.metrowatch.kochi.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


class NavigationResults {

    private val results = mutableMapOf<String, Any?>()
    var version by mutableStateOf(0)
        private set

    fun <T> set(key: String, value: T) {
        results[key] = value
        version ++
    }

    fun <T> consume(key: String): T? {
        return results.remove(key) as? T
    }

    fun clearResult(key: String) {
        results.remove(key)
    }
}
