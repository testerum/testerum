package com.testerum.common_jdk

import java.nio.file.Files
import java.nio.file.Path
import java.util.*

fun loadPropertiesFrom(path: Path): Properties {
    val result = Properties()

    Files.newInputStream(path).use { inputStream ->
        result.load(inputStream)
    }

    return result
}

@Suppress("UNCHECKED_CAST")
fun Properties.asMap(): Map<String, String> = this as Map<String, String>
