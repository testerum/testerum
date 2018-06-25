package com.testerum.test_file_format.stepdef.signature

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.step_call.phase.FileStepPhaseSerializer
import com.testerum.test_file_format.stepdef.signature.part.FileStepDefSignaturePartSerializer
import java.io.Writer

object FileStepDefSignatureSerializer : BaseSerializer<FileStepDefSignature>() {

    override fun serialize(source: FileStepDefSignature, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        destination.write("step-def: ")
        FileStepPhaseSerializer.serialize(source.phase, destination, indentLevel)
        destination.write(" ")

        for (part in source.parts) {
            FileStepDefSignaturePartSerializer.serialize(part, destination, indentLevel)
        }
    }

}