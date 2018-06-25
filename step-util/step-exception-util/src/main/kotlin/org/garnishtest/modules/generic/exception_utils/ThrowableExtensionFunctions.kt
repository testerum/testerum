package org.garnishtest.modules.generic.exception_utils

import java.io.PrintWriter
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