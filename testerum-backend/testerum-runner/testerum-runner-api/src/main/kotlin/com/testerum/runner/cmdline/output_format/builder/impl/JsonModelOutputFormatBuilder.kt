package com.testerum.runner.cmdline.output_format.builder.impl

import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.builder.EventListenerProperties
import com.testerum.runner.cmdline.output_format.builder.GenericOutputFormatBuilder
import java.nio.file.Path

class JsonModelOutputFormatBuilder {

    var destinationFileDirectory: Path? = null
    var formatted: Boolean? = null

    fun build(): String {
        val builder = GenericOutputFormatBuilder(OutputFormat.JSON_MODEL)

        destinationFileDirectory?.let {
            builder.properties[EventListenerProperties.JsonModel.DESTINATION_DIRECTORY] = it.toAbsolutePath().normalize().toString()
        }

        formatted?.let {
            builder.properties[EventListenerProperties.JsonModel.FORMATTED] = it.toString()
        }

        return builder.build()
    }
}
