val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val lettuce_version: String by project
val coroutines_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    java
}

group = "ru.evreke.crawler"
version = "0.0.1"
application {
    mainClass.set("ru.evreke.crawler.ApplicationKt")
    mainClassName = application.mainClass.get()

}

tasks {
    shadowJar {

        manifest { attributes(Pair("Main-Class", application.mainClassName)) }
        archiveFileName.set("crawler.jar")
    }

    jar {
        manifest { attributes["Main-Class"] = application.mainClass }
        archiveFileName.set("crawler.jar")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.3.0")
}