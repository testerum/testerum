package com.testerum.test_file_format.common.step_call.part.arg_part

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileArgPartSerializer : BaseSerializer<FileArgPart>() {

    override fun serialize(source: FileArgPart, destination: Writer, indentLevel: Int) {
        when (source) {
            is FileTextArgPart -> destination.write(source.text)
            is FileExpressionArgPart -> {
                destination.write("{{")
                destination.write(
                        source.text
                              .replace("\\", "\\\\")
                              .replace("}}", "\\}}")
                )
                destination.write("}}")
            }
        }
    }

}