package com.testerum.test_file_format.testdef

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallSerializer
import com.testerum.test_file_format.common.tags.FileTagsSerializer
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties
import com.testerum.test_file_format.testdef.properties.FileTestDefPropertiesSerializer
import java.io.Writer

object FileTestDefSerializer : BaseSerializer<FileTestDef>() {

    // todo: tests for this class & other classes that are not tested

    override fun serialize(source: FileTestDef, destination: Writer, indentLevel: Int) {
        serializeTestName(source.name, destination, indentLevel)
        serializeTestProperties(source.properties, destination, indentLevel + 1)
        serializeDescription(source.description, destination, indentLevel + 1)
        serializeTags(source.tags, destination, indentLevel + 1)
        serializeSteps(source.steps, destination, indentLevel + 1)
    }

    private fun serializeTestName(testName: String, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        destination.write("test-def: ")
        destination.write(testName)
        destination.write("\n")
    }

    private fun serializeTestProperties(properties: FileTestDefProperties, destination: Writer, indentLevel: Int) {
        if (properties.isEmpty()) {
            return
        }

        destination.write("\n")
        FileTestDefPropertiesSerializer.serialize(properties, destination, indentLevel)
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

    private fun serializeSteps(steps: List<FileStepCall>, destination: Writer, indentLevel: Int) {
        if (steps.isEmpty()) {
            return
        }

        destination.write("\n")

        for (step in steps) {
            FileStepCallSerializer.serialize(step, destination, indentLevel)
        }
    }

}
