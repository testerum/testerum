package com.testerum.test_file_format.testdef

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import com.testerum.test_file_format.common.step_call.FileStepCallSerializer
import java.io.Writer

object FileTestDefSerializer : BaseSerializer<FileTestDef>() {

    // todo: tests for this class & other classes that are not tested

    override fun serialize(source: FileTestDef, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)

        destination.write("test-def: ")
        destination.write(source.name)
        destination.write("\n")

        source.description?.let {
            destination.write("\n")
            FileDescriptionSerializer.serialize(it, destination, indentLevel + 1)
            destination.write("\n")
        }

        destination.write("\n")

        for (step in source.steps) {
            FileStepCallSerializer.serialize(step, destination, indentLevel + 1)
        }
    }

}
