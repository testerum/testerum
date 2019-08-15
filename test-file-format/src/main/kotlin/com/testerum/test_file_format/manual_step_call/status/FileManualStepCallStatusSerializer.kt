package com.testerum.test_file_format.manual_step_call.status

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileManualStepCallStatusSerializer : BaseSerializer<FileManualStepCallProperties>() {

    override fun serialize(source: FileManualStepCallProperties, destination: Writer, indentLevel: Int) {
        destination.write("[")

        destination.write(source.status.name)

        if (!source.enabled) {
            destination.write(", disabled")
        }

        destination.write("]")
    }

}
