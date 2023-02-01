package com.testerum.common.serializing

import java.io.StringWriter
import java.io.Writer

interface Serializer<in T> {

    fun serialize(source: T,
                  destination: Writer,
                  indentLevel: Int)

    fun serializeToString(source: T, indentLevel: Int = 0): String {
        val destination = StringWriter()

        serialize(source, destination, indentLevel)

        return destination.toString()
    }

}