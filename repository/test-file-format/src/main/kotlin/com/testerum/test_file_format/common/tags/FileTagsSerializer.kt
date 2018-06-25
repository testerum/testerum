package com.testerum.test_file_format.common.tags

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileTagsSerializer : BaseSerializer<List<String>>() {

    override fun serialize(source: List<String>, destination: Writer, indentLevel: Int) {
        validate(source)

        indent(destination, indentLevel)
        destination.write("tags = <<")
        destination.write(source.joinToString(separator = ", "))
        destination.write(">>\n")
    }

    private fun validate(tags: List<String>) {
        for (tag in tags) {
            validate(tag)
        }
    }

    private fun validate(tag: String) {
        if (tag.contains(",")) {
            throw IllegalArgumentException("cannot serialize tag that contains comma; but found [$tag]")
        }

        if (tag.contains(">>")) {
            throw IllegalArgumentException("cannot serialize tag that contains >>; but found [$tag]")
        }
    }

}