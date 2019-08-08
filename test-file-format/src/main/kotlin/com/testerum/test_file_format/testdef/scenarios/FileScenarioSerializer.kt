package com.testerum.test_file_format.testdef.scenarios

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileScenarioSerializer : BaseSerializer<FileScenario>() {

    override fun serialize(source: FileScenario, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        serializeScenarioLine(source, destination, indentLevel)
        serializeParams(source, destination, indentLevel)
    }

    private fun serializeScenarioLine(source: FileScenario, destination: Writer, indentLevel: Int) {
        destination.write("scenario:")

        if (source.name != null) {
            destination.write(" ")
            destination.write(source.name)
        }

        destination.write("\n")
    }

    private fun serializeParams(source: FileScenario, destination: Writer, indentLevel: Int) {
        for (param in source.params) {
            FileScenarioParamSerializer.serialize(param, destination, indentLevel + 1)
            destination.write("\n")
        }
    }

}
