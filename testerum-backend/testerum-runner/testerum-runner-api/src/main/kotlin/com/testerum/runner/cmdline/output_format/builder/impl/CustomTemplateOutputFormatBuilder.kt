package com.testerum.runner.cmdline.output_format.builder.impl

import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.builder.EventListenerProperties
import com.testerum.runner.cmdline.output_format.builder.GenericOutputFormatBuilder
import java.nio.file.Path

class CustomTemplateOutputFormatBuilder {

    var scriptFile: Path? = null

    fun build(): String {
        val builder = GenericOutputFormatBuilder(OutputFormat.CUSTOM_TEMPLATE)

        scriptFile?.let {
            builder.properties[EventListenerProperties.CustomTemplate.SCRIPT_FILE] = it.toAbsolutePath().normalize().toString()
        }

        return builder.build()
    }
}
