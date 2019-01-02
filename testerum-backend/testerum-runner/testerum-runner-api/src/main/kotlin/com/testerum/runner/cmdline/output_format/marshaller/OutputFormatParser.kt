package com.testerum.runner.cmdline.output_format.marshaller

import com.testerum.runner.cmdline.output_format.OutputFormat
import com.testerum.runner.cmdline.output_format.marshaller.properties.RunnerPropertiesParser

object OutputFormatParser {

    fun parse(outputFormatWithProperties: String): Pair<OutputFormat, Map<String, String>> {
        val indexOfColon = outputFormatWithProperties.indexOf(":")

        val outputFormatString: String
        val propertiesString: String

        if (indexOfColon == -1) {
            outputFormatString = outputFormatWithProperties
            propertiesString = ""
        } else {
            outputFormatString = outputFormatWithProperties.substring(0, indexOfColon)
            propertiesString = outputFormatWithProperties.substring(indexOfColon + 1)
        }

        val outputFormat = OutputFormat.parse(outputFormatString)
        val properties = RunnerPropertiesParser.parse(propertiesString)

        return Pair(outputFormat, properties)
    }

}
