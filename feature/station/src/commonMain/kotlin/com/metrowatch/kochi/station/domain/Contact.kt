package com.metrowatch.kochi.station.domain

data class Contact(
    val id: Int,
    val category: String,
    val name: String,
    val displayName: String,
    val value: String
)
