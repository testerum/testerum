package com.testerum.runner.cmdline.output_format.builder.impl

import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.builder.EventListenerProperties
import com.testerum.runner.cmdline.output_format.builder.GenericOutputFormatBuilder
import java.nio.file.Path

class PrettyOutputFormatBuilder {

    var destinationDirectory: Path? = null

    fun build(): String {
        val builder = GenericOutputFormatBuilder(OutputFormat.PRETTY)

        destinationDirectory?.let {
            builder.properties[EventListenerProperties.Pretty.DESTINATION_DIRECTORY] = it.toAbsolutePath().normalize().toString()
        }

        return builder.build()
    }
}
