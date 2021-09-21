package ru.evreke.crawler.model

import kotlinx.serialization.Serializable

@Serializable
data class Site(
    val url: String,
    val links: List<String>,
)