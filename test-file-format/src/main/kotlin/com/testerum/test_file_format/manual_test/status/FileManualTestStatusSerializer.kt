package com.testerum.test_file_format.manual_test.status

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileManualTestStatusSerializer : BaseSerializer<FileManualTestStatus>() {

    override fun serialize(source: FileManualTestStatus, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        destination.write("status = ")
        destination.write(source.name)
        destination.write("\n")
    }

}