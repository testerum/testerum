package com.testerum.test_file_format.common.step_call.`var`

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.util.escapeMultiLineString
import java.io.Writer

object FileStepVarSerializer : BaseSerializer<FileStepVar>() {

    override fun serialize(source: FileStepVar, destination: Writer, indentLevel: Int) {
        val valueLines = source.value
            .lines()

        val minCommonIndent = valueLines
            .filter(String::isNotBlank)
            .map { indentWidth(it) }
            .minOrNull() ?: 0

        val lines = valueLines.map {
            it.drop(minCommonIndent)
        }



        if (valueLines.size == 1) {
            serializeSingleLine(destination, indentLevel, source.name, lines)
        } else {
            serializeMultiLine(destination, indentLevel, source.name, lines)
        }
    }

    private fun serializeSingleLine(destination: Writer, indentLevel: Int, name: String, valueLines: List<String>) {
        indent(destination, indentLevel)
        destination.write("var ")
        destination.write(name)
        destination.write(" = <<")
        destination.write(escapeMultiLineString(valueLines.first()))
        destination.write(">>")
    }

    private fun serializeMultiLine(destination: Writer, indentLevel: Int, name: String, valueLines: List<String>) {
        indent(destination, indentLevel)
        destination.write("var ")
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

}
