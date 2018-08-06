package com.testerum.common_jdk.throwable

import java.io.StringWriter

fun <T : Throwable?> T.toStringWithStacktrace(): String {
    if (this == null) {
        return "null"
    }

    val result = StringWriter()

    this.printStackTrace(
            java.io.PrintWriter(result)
    )

    return result.toString()
}