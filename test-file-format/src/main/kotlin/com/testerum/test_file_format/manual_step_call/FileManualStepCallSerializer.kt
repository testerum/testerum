package com.testerum.test_file_format.manual_step_call

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.step_call.`var`.FileStepVarSerializer
import com.testerum.test_file_format.common.step_call.part.FileStepCallPartSerializer
import com.testerum.test_file_format.common.step_call.phase.FileStepPhaseSerializer
import java.io.Writer

object FileManualStepCallSerializer: BaseSerializer<FileManualStepCall>() {

    override fun serialize(source: FileManualStepCall, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        serializeStepCallLine(source, destination, indentLevel)
        serializeVariables(source, destination, indentLevel)
    }

    private fun serializeStepCallLine(source: FileManualStepCall, destination: Writer, indentLevel: Int) {
        destination.write("step [")
        destination.write(source.status.name)
        destination.write("]: ")

        FileStepPhaseSerializer.serialize(source.step.phase, destination, indentLevel)
        destination.write(" ")

        for (part in source.step.parts) {
            FileStepCallPartSerializer.serialize(part, destination, indentLevel)
        }

        destination.write("\n")
    }

    private fun serializeVariables(source: FileManualStepCall, destination: Writer, indentLevel: Int) {
        for (variable in source.step.vars) {
            FileStepVarSerializer.serialize(variable, destination, indentLevel + 1)
            destination.write("\n")
        }
    }

}