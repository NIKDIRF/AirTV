package com.airnet.domain.channels

data class Channel(
    val id: String,
    val name: String,
    val url: String,
    val group: String?,
    val logo: String?
)