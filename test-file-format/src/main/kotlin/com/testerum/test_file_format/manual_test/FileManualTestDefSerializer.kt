package com.testerum.test_file_format.manual_test

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import com.testerum.test_file_format.common.tags.FileTagsSerializer
import com.testerum.test_file_format.manual_step_call.FileManualStepCall
import com.testerum.test_file_format.manual_step_call.FileManualStepCallSerializer
import com.testerum.test_file_format.manual_test.comments.FileManualCommentsSerializer
import com.testerum.test_file_format.manual_test.status.FileManualTestStatus
import com.testerum.test_file_format.manual_test.status.FileManualTestStatusSerializer
import java.io.Writer

object FileManualTestDefSerializer : BaseSerializer<FileManualTestDef>() {

    override fun serialize(source: FileManualTestDef, destination: Writer, indentLevel: Int) {
        serializeTestName(source.name, destination, indentLevel)
        serializeDescription(source.description, destination, indentLevel + 1)
        serializeTags(source.tags, destination, indentLevel + 1)
        serializeSteps(source.steps, destination, indentLevel + 1)
        serializeStatus(source.status, destination, indentLevel + 1)
        serializeComments(source.comments, destination, indentLevel + 1)
    }

    private fun serializeTestName(testName: String, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        destination.write("test-def: ")
        destination.write(testName)
        destination.write("\n")
    }

    private fun serializeDescription(description: String?, destination: Writer, indentLevel: Int) {
        if (description == null || description.isEmpty()) {
            return
        }

        destination.write("\n")
        FileDescriptionSerializer.serialize(description, destination, indentLevel)
    }

    private fun serializeTags(tags: List<String>, destination: Writer, indentLevel: Int) {
        if (!tags.isNotEmpty()) {
            return
        }

        destination.write("\n")
        FileTagsSerializer.serialize(tags, destination, indentLevel)
    }

    private fun serializeSteps(steps: List<FileManualStepCall>, destination: Writer, indentLevel: Int) {
        if (steps.isEmpty()) {
            return
        }

        destination.write("\n")

        for (step in steps) {
            FileManualStepCallSerializer.serialize(step, destination, indentLevel)
        }
    }

    private fun serializeStatus(status: FileManualTestStatus, destination: Writer, indentLevel: Int) {
        destination.write("\n")
        FileManualTestStatusSerializer.serialize(status, destination, indentLevel)
    }

    private fun serializeComments(comments: String?, destination: Writer, indentLevel: Int) {
        if (comments == null || comments.isEmpty()) {
            return
        }

        destination.write("\n")
         FileManualCommentsSerializer.serialize(comments, destination, indentLevel)
    }

}
