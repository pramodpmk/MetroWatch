package com.fungames.fare.domain

data class GeneralFare(
    val title: String,
    val fare: String,
    val distance: String = ""
)