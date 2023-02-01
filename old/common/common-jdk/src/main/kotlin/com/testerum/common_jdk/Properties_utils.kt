package com.testerum.common_jdk

import java.nio.file.Files
import java.util.*
import java.nio.file.Path as JavaPath

fun loadPropertiesFrom(path: JavaPath): Properties {
    val result = Properties()

    Files.newInputStream(path).use { inputStream ->
        result.load(inputStream)
    }

    return result
}

// the corresponding extension function "Properties.toProperties()" is part of Kotlin stdlib
@Suppress("UNCHECKED_CAST")
fun Properties.asMap(): Map<String, String> = this as Map<String, String>
