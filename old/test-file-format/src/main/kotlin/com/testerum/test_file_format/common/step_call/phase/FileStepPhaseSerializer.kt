package com.testerum.test_file_format.common.step_call.phase

import com.testerum.common.serializing.BaseSerializer
import java.io.Writer

object FileStepPhaseSerializer : BaseSerializer<FileStepPhase>() {

    override fun serialize(source: FileStepPhase, destination: Writer, indentLevel: Int) {
        destination.write(source.code)
    }

}