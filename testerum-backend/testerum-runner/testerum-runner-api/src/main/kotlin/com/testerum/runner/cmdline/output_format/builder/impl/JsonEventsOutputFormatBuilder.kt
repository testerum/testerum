package com.testerum.runner.cmdline.output_format.builder.impl

import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.builder.EventListenerProperties
import com.testerum.runner.cmdline.output_format.builder.GenericOutputFormatBuilder
import java.nio.file.Path

class JsonEventsOutputFormatBuilder {

    var destinationFileName: Path? = null

    fun build(): String {
        val builder = GenericOutputFormatBuilder(OutputFormat.JSON_EVENTS)

        destinationFileName?.let {
            builder.properties[EventListenerProperties.JsonEvents.DESTINATION_FILE_NAME] = it.toAbsolutePath().normalize().toString()
        }

        return builder.build()
    }
}
