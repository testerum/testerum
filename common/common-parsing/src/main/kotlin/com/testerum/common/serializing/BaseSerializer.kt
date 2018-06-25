package com.testerum.common.serializing

import java.io.Writer

abstract class BaseSerializer<in T> : Serializer<T> {

    protected fun indent(destination: Writer, indentLevel: Int) {
        if (indentLevel < 0) {
            throw IllegalArgumentException("indentLevel must not be negative, but was [$indentLevel]")
        }

        repeat(indentLevel) {
            destination.write("    ")
        }
    }

}