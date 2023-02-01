package com.testerum.common_fast_serialization.debug

import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_kotlin.doesNotExist
import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("SYNTAX: DumpSerializedFileKt </path/to/file.tfsf>")
        exitProcess(1)
    }

    val filePath = Paths.get(args[0])
        .toAbsolutePath()
        .normalize()
    if (filePath.doesNotExist) {
        System.err.println("ERROR: file at path [$filePath] does not exist")
        exitProcess(2)
    }

    val fastInput = BufferedInputStream(Files.newInputStream(filePath)).use { inputStream ->
        FastInput.readFrom(inputStream)
    }

    val sortedMap = fastInput.asSortedMap()
    if (sortedMap.isEmpty()) {
        println(">>>>> the file is empty <<<<<")
        return
    }

    for ((key, value) in sortedMap) {
        val keyForDisplay = "[$key]"
        val valueForDisplay = "[$value]"

        println("$keyForDisplay = $valueForDisplay")
    }
}
