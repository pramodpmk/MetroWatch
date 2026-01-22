package com.fungames.core.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation result types for CMP-safe result passing.
 * These are serializable and can be passed between screens.
 */

/**
 * Result from StationPicker screen.
 * Contains the selected station's id and name.
 */
@Serializable
data class StationPickerResult(
    val id: Int,
    val name: String
)

