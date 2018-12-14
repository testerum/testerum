package com.testerum.runner.cmdline

import java.nio.file.Path as JavaPath

object EventListenerProperties {
    object JsonEvents {
        const val DESTINATION_FILE_NAME = "destinationFileName"
    }
}

class OutputFormatBuilder(private val outputFormat: OutputFormat) {

    companion object {
        private val ESCAPE_PROPERTY_REGEX = Regex("([,=])")
        private val ESCAPE_PROPERTY_REPLACEMENT = "\\\\$1"
    }

    val properties = LinkedHashMap<String, String?>()

    fun build(): String = buildString {
        append(outputFormat.name).append(':')

        var first = true
        for ((key, value) in properties) {
            if (first) {
                first = false
            } else {
                append(',')
            }

            append(key.escapeProperty())
            if (value != null) {
                append("=")
                append(value.escapeProperty())
            }
        }
    }

    private fun String.escapeProperty() = this.replace(ESCAPE_PROPERTY_REGEX, ESCAPE_PROPERTY_REPLACEMENT)
}

class JsonEventsOutputFormatBuilder {

    var destinationFileName: JavaPath? = null

    fun build(): String {
        val builder = OutputFormatBuilder(OutputFormat.JSON_EVENTS)

        destinationFileName?.let {
            builder.properties[EventListenerProperties.JsonEvents.DESTINATION_FILE_NAME] = it.toAbsolutePath().normalize().toString()
        }

        return builder.build()
    }
}

fun jsonEventsOutputFormat(body: JsonEventsOutputFormatBuilder.() -> Unit): String {
    val builder = JsonEventsOutputFormatBuilder()

    builder.body()

    return builder.build()
}

fun main(args: Array<String>) {
    val builder = OutputFormatBuilder(OutputFormat.JSON_EVENTS)

    "all together now" to "equals=/comma,/works"

    builder.properties["equation"]         = "1+2=3"
    builder.properties["greeting"]         = "Hello, world!"
    builder.properties["all together now"] = "equals=/comma,/works"

    println(
            builder.build()
    )
}