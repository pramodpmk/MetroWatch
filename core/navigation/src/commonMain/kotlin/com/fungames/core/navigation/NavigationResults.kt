package com.fungames.core.navigation


class NavigationResults {

    private val results = mutableMapOf<String, Any?>()

    fun <T> set(key: String, value: T) {
        results[key] = value
    }

    fun <T> consume(key: String): T? {
        return results.remove(key) as? T
    }

    fun clearResult(key: String) {
        results.remove(key)
    }
}
