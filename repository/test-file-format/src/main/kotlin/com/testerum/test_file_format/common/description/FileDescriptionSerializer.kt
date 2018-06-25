package com.testerum.test_file_format.common.description

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.util.escapeMultiLineString
import java.io.Writer

object FileDescriptionSerializer : BaseSerializer<String>() {

    override fun serialize(source: String, destination: Writer, indentLevel: Int) {
        val lines = source.trimIndent()
                          .lines()

        if (lines.size == 1) {
            serializeSingleLine(destination, indentLevel, lines)
        } else {
            serializeMultiLine(destination, indentLevel, lines)
        }
    }

    private fun serializeSingleLine(destination: Writer, indentLevel: Int, lines: List<String>) {
        indent(destination, indentLevel)
        destination.write("description = <<")
        destination.write(escapeMultiLineString(lines.first()))
        destination.write(">>")
    }

    private fun serializeMultiLine(destination: Writer, indentLevel: Int, lines: List<String>) {
        indent(destination, indentLevel)
        destination.write("description = <<\n")

        lines.forEach { line ->
            indent(destination, indentLevel + 1)

            destination.write(escapeMultiLineString(line))

            destination.write("\n")
        }

        indent(destination, indentLevel)
        destination.write(">>")
    }

}