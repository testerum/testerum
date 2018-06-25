package com.testerum.runner.events.model.error

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ExceptionDetail @JsonCreator constructor(
        @JsonProperty("exceptionClassName") val exceptionClassName: String,
        @JsonProperty("message")            val message: String? = null,
        @JsonProperty("stackTrace")         val stackTrace: List<ExceptionStackTraceElementDetail> = emptyList(),
        @JsonProperty("cause")              val cause: ExceptionDetail? = null,
        @JsonProperty("suppressed")         val suppressed: List<ExceptionDetail> = emptyList()
) {

    companion object {

        fun fromThrowable(throwable: Throwable): ExceptionDetail {
            val throwableCause = throwable.cause
            val cause = if (throwableCause == null) {
                null
            } else {
                fromThrowable(throwableCause)
            }

            return ExceptionDetail(
                    exceptionClassName = throwable.javaClass.name,
                    message            = throwable.message,
                    stackTrace         = throwable.stackTrace.map { ExceptionStackTraceElementDetail.fromStackTraceElement(it) },
                    cause              = cause,
                    suppressed         = throwable.suppressed?.map { fromThrowable(it) } ?: emptyList()
            )
        }
    }

    override fun toString() = buildString { toString(this) }

    private fun toString(destination: StringBuilder) {
        destination.append(exceptionClassName)

        if (message != null) {
            destination.append(": ").append(message)
        }
    }

    fun detailedToString() = buildString { detailedToString(this) }

    private fun detailedToString(destination: StringBuilder) {
        val processed: MutableSet<ExceptionDetail> = Collections.newSetFromMap(
                IdentityHashMap<ExceptionDetail, Boolean>()
        )

        processed += this

        // print our stack trace
        toString(destination)
        destination.append("\n")
        for (traceElement in stackTrace) {
            destination.append("\tat ").append(traceElement).append("\n")
        }

        // print suppressed exceptions, if any
        for (suppressedException in suppressed) {
            suppressedException.detailedToStringEnclosed(
                    destination    = destination,
                    enclosingTrace = stackTrace,
                    caption        = "Suppressed: ",
                    prefix         = "\t",
                    processed      = processed
            )
        }

        // print cause, if any
        if (cause != null) {
            cause.detailedToStringEnclosed(
                    destination    = destination,
                    enclosingTrace = stackTrace,
                    caption        = "Caused by: ",
                    prefix         = "",
                    processed      = processed
            )
        }
    }

    private fun detailedToStringEnclosed(destination: StringBuilder,
                                         enclosingTrace: List<ExceptionStackTraceElementDetail>,
                                         caption: String,
                                         prefix: String,
                                         processed: MutableSet<ExceptionDetail>) {
        if (processed.contains(this)) {
            destination.append("\t[CIRCULAR REFERENCE:").append(this).append("]").append("\n")
        } else {
            processed += this

            // compute number of frames in common between this and enclosing
            var ourIndex: Int = stackTrace.size - 1
            var enclosingIndex: Int = enclosingTrace.size - 1

            while (ourIndex >= 0 && enclosingIndex >= 0 && stackTrace[ourIndex] == enclosingTrace[enclosingIndex]) {
                ourIndex--
                enclosingIndex--
            }
            val framesInCommon: Int = stackTrace.size - 1 - ourIndex

            // print our stack trace
            destination.append(prefix)
            destination.append(caption)
            toString(destination)
            destination.append("\n")
            for (i in 0..ourIndex) {
                destination.append(prefix)
                destination.append("\tat ").append(stackTrace[i]).append("\n")
            }
            if (framesInCommon != 0) {
                destination.append(prefix)
                destination.append("\t... ").append(framesInCommon).append(" more").append("\n")
            }

            // print suppressed exceptions, if any
            for (suppressedException in suppressed) {
                suppressedException.detailedToStringEnclosed(
                        destination    = destination,
                        enclosingTrace = stackTrace,
                        caption        = "Suppressed: ",
                        prefix         = prefix + "\t",
                        processed      = processed
                )
            }

            // print cause, if any
            if (cause != null) {
                cause.detailedToStringEnclosed(
                        destination    = destination,
                        enclosingTrace = stackTrace,
                        caption        = "Caused by: ",
                        prefix         = prefix,
                        processed      = processed
                )
            }
        }
    }
}