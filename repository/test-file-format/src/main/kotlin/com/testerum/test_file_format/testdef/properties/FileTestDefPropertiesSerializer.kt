package com.testerum.test_file_format.testdef.properties

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileTestDefPropertiesSerializer : BaseSerializer<FileTestDefProperties>() {

    override fun serialize(source: FileTestDefProperties, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        destination.write("test-properties = <<")
        destination.write(source.toList().joinToString(separator = ", "))
        destination.write(">>\n")
    }

    private fun FileTestDefProperties.toList(): List<String> = mutableListOf<String>().apply {
        if (isManual) {
            add("manual")
        }
        if (isDisabled) {
            add("disabled")
        }
    }

}