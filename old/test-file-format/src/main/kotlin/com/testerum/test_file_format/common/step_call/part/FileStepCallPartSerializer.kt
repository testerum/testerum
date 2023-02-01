package com.testerum.test_file_format.common.step_call.part

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileStepCallPartSerializer : BaseSerializer<FileStepCallPart>() {

    override fun serialize(source: FileStepCallPart, destination: Writer, indentLevel: Int) {
        when (source) {
            is FileTextStepCallPart -> destination.write(
                    source.text
                            .replace("<<", "\\<<")
            )
            is FileArgStepCallPart -> {
                destination.write("<<")

                var escaped = source.text
                escaped = escaped.replace(">", "\\>")
                destination.write(escaped)
                destination.write(">>")
            }
        }
    }

}
