package com.testerum.test_file_format.manual_test_plan

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import java.io.Writer
import java.time.LocalDateTime

object FileManualTestPlanSerializer : BaseSerializer<FileManualTestPlan>() {

    override fun serialize(source: FileManualTestPlan, destination: Writer, indentLevel: Int) {
        serializeDescription(source.description, destination, indentLevel)
        serializeLocalDateTime("created-date-utc", source.createdDateUtc, destination, indentLevel)
        serializeLocalDateTime("finalized-date-utc", source.finalizedDateUtc, destination, indentLevel)
    }

    private fun serializeDescription(description: String?, destination: Writer, indentLevel: Int) {
        if (description == null || description.isEmpty()) {
            return
        }

        FileDescriptionSerializer.serialize(description, destination, indentLevel)
    }

    private fun serializeLocalDateTime(label: String, localDateTime: LocalDateTime?, destination: Writer, indentLevel: Int) {
        if (localDateTime == null) {
            return
        }

        destination.write("\n")

        indent(destination, indentLevel)
        destination.write("$label = ")
        destination.write(localDateTime.toString())
        destination.write("\n")
    }

}
