package ru.evreke.crawler.core

val httpsRegexp = """https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)""".toRegex()
val anchorRef = """<a.+?\s*href\s*=\s*["\']?([^"\'\s>]+)["\']?""".toRegex()

fun String.parsePageUrls(pageSource: String): List<String> = anchorRef.findAll(pageSource).map { it.groupValues.last() }.toList()
fun String.parsePageUrls(): List<String> = anchorRef.findAll(this).map { it.groupValues.last() }.toList()