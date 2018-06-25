package com.testerum.test_file_format.stepdef

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import com.testerum.test_file_format.common.step_call.FileStepCallSerializer
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignatureSerializer
import java.io.Writer

object FileStepDefSerializer : BaseSerializer<FileStepDef>() {

    // todo: tests for this class

    override fun serialize(source: FileStepDef, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        FileStepDefSignatureSerializer.serialize(source.signature, destination, indentLevel)
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
