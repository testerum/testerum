package com.testerum.test_file_format.stepdef

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.description.FileDescriptionSerializer
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallSerializer
import com.testerum.test_file_format.common.tags.FileTagsSerializer
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignature
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignatureSerializer
import com.testerum.test_file_format.testdef.FileTestDefSerializer
import java.io.Writer

object FileStepDefSerializer : BaseSerializer<FileStepDef>() {

    override fun serialize(source: FileStepDef, destination: Writer, indentLevel: Int) {
        serializeSignature(source.signature, destination, indentLevel)
        serializeDescription(source.description, destination, indentLevel + 1)

        destination.write("\n")

        serializeTags(source.tags, destination, indentLevel + 1)
        serializeSteps(source.steps, destination, indentLevel + 1)
    }

    private fun serializeSignature(signature: FileStepDefSignature, destination: Writer, indentLevel: Int) {
        FileStepDefSignatureSerializer.serialize(signature, destination, indentLevel)
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
