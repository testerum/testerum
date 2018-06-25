package com.testerum.runner.events.model.error

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ExceptionStackTraceElementDetail @JsonCreator constructor(
        @JsonProperty("className")  val className: String,
        @JsonProperty("methodName") val methodName: String,
        @JsonProperty("fileName")   val fileName: String?,
        @JsonProperty("lineNumber") val lineNumber: Int
) {

    companion object {

        fun fromStackTraceElement(stackTraceElement: StackTraceElement)
            = ExceptionStackTraceElementDetail(
                className  = stackTraceElement.className,
                methodName = stackTraceElement.methodName,
                fileName   = stackTraceElement.fileName,
                lineNumber = stackTraceElement.lineNumber
            )
    }

    // todo: extract jar name, like LogBack

    val nativeMethod: Boolean
        get() = lineNumber == -2

    override fun toString() = buildString {
        append(className)
        append(".")
        append(methodName)

        append("(")

        if (nativeMethod) {
            append("Native Method")
        } else {
            if (fileName != null) {
                append(fileName)

                if (lineNumber >= 0) {
                    append(":").append(lineNumber)
                }
            } else {
                append("Unknown Source")
            }
        }

        append(")")
    }
}
