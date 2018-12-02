package com.testerum.common_kotlin

import java.io.PrintWriter
import java.io.StringWriter

val Throwable.rootCause: Throwable
    get() {
        var root: Throwable = this
        var cause = root.cause

        while (cause != null) {
            root = cause
            cause = cause.cause
        }

        return root
    }

fun Throwable.printStackTraceToString(): String {
    val result = StringWriter()

    this.printStackTrace(
            PrintWriter(result)
    )

    return result.toString()
}