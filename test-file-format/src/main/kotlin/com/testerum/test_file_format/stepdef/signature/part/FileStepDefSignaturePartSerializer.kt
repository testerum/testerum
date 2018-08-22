package com.testerum.test_file_format.stepdef.signature.part

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileStepDefSignaturePartSerializer : BaseSerializer<FileStepDefSignaturePart>() {

    override fun serialize(source: FileStepDefSignaturePart, destination: Writer, indentLevel: Int) {
        when (source) {
            is FileTextStepDefSignaturePart -> destination.write(source.text)
            is FileParamStepDefSignaturePart -> {
                destination.write("<<")
                destination.write(source.name)
                destination.write(": ")
                destination.write(source.type)
                destination.write(">>")
            }
        }
    }
}
