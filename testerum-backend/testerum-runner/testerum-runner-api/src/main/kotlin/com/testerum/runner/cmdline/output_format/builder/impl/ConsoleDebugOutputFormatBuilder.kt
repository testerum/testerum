package com.testerum.runner.cmdline.output_format.builder.impl

import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.builder.GenericOutputFormatBuilder

class ConsoleDebugOutputFormatBuilder {

    fun build(): String {
        val builder = GenericOutputFormatBuilder(OutputFormat.CONSOLE_DEBUG)

        return builder.build()
    }

}
