package com.metrowatch.kochi.data

fun String.toSentenceCase(): String {
    if (this.isEmpty()) return this
    // Handle snake_case
    val spaced = this.replace("_", " ")
    // Handle camelCase/PascalCase
    val withSpaces = spaced.replace(Regex("([a-z])([A-Z])"), "$1 $2")
    return withSpaces.lowercase().replaceFirstChar { it.uppercase() }
}
