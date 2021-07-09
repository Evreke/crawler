val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val lettuce_version: String by project
val coroutines_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
}

group = "ru.evreke.crawler"
version = "0.0.1"
application {
    mainClass.set("ru.evreke.crawler.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutines_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.lettuce:lettuce-core:$lettuce_version")
    implementation("org.jgrapht:jgrapht-core:1.5.1")
    implementation("org.jgrapht:jgrapht-io:1.5.1")
}