package com.testerum.test_file_format.manual_step_call.status

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileManualStepCallStatusSerializer : BaseSerializer<FileManualStepCallStatus>() {

    override fun serialize(source: FileManualStepCallStatus, destination: Writer, indentLevel: Int) {
        destination.write("[")
        destination.write(source.name)
        destination.write("]")
    }

}