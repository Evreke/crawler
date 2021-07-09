package ru.evreke.crawler.core

import io.ktor.client.*
import io.ktor.client.engine.cio.*

val client = HttpClient(CIO)