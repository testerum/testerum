package com.testerum.test_file_format.testdef

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallSerializer
import com.testerum.test_file_format.common.tags.FileTagsSerializer
import com.testerum.test_file_format.feature.FileFeatureSerializer
import java.io.Writer

object FileTestDefSerializer : BaseSerializer<FileTestDef>() {

    // todo: tests for this class & other classes that are not tested

    override fun serialize(source: FileTestDef, destination: Writer, indentLevel: Int) {
        serializeTestName(source.name, destination, indentLevel)
        serializeDescription(source.description, destination, indentLevel + 1)

        destination.write("\n")

        serializeTags(source.tags, destination, indentLevel + 1)
        serializeSteps(source.steps, destination, indentLevel + 1)
    }

    private fun serializeTestName(testName: String, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        destination.write("test-def: ")
        destination.write(testName)
        destination.write("\n")
    }

    private fun serializeDescription(description: String?, destination: Writer, indentLevel: Int) {
        description?.let {
            destination.write("\n")
            FileDescriptionSerializer.serialize(it, destination, indentLevel)
            destination.write("\n")
        }
    }

    private fun serializeTags(tags: List<String>, destination: Writer, indentLevel: Int) {
        if (tags.isNotEmpty()) {
            FileTagsSerializer.serialize(tags, destination, indentLevel)
            destination.write("\n")
        }
    }

    private fun serializeSteps(steps: List<FileStepCall>, destination: Writer, indentLevel: Int) {
        for (step in steps) {
            FileStepCallSerializer.serialize(step, destination, indentLevel)
        }
    }

}
