package com.testerum.test_file_format.testdef.scenarios

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.util.escapeMultiLineString
import java.io.Writer

object FileScenarioParamSerializer : BaseSerializer<FileScenarioParam>() {

    override fun serialize(source: FileScenarioParam, destination: Writer, indentLevel: Int) {
        val valueLines = source.value
                .lines()

        val minCommonIndent = valueLines
                .filter(String::isNotBlank)
                .map { indentWidth(it) }
                .min() ?: 0

        val lines = valueLines.map {
            it.drop(minCommonIndent)
        }

        if (valueLines.size == 1) {
            serializeSingleLine(destination, indentLevel, source.name, source.type, lines)
        } else {
            serializeMultiLine(destination, indentLevel, source.name, source.type, lines)
        }
    }

    private fun serializeSingleLine(destination: Writer,
                                    indentLevel: Int,
                                    name: String,
                                    type: FileScenarioParamType,
                                    valueLines: List<String>) {
        indent(destination, indentLevel)
        destination.write(
                getSerializedParamType(type)
        )
        destination.write(" ")
        destination.write(name)
        destination.write(" = <<")
        destination.write(escapeMultiLineString(valueLines.first()))
        destination.write(">>")
    }

    private fun serializeMultiLine(destination: Writer,
                                   indentLevel: Int,
                                   name: String,
                                   type: FileScenarioParamType,
                                   valueLines: List<String>) {
        indent(destination, indentLevel)
        destination.write(
                getSerializedParamType(type)
        )
        destination.write(" ")
        destination.write(name)
        destination.write(" = <<\n")

        valueLines.forEach { line ->
            indent(destination, indentLevel + 1)

            destination.write(escapeMultiLineString(line))

            destination.write("\n")
        }

        indent(destination, indentLevel)
        destination.write(">>")
    }

    private fun indentWidth(line: String): Int = line.indexOfFirst { !it.isWhitespace() }.let { if (it == -1) line.length else it }

    private fun getSerializedParamType(type: FileScenarioParamType): String {
        return when (type) {
            FileScenarioParamType.TEXT -> "param"
            FileScenarioParamType.JSON -> "param-json"
        }
    }

}
