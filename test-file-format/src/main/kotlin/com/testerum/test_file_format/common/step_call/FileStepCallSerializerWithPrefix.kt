package com.testerum.test_file_format.common.step_call

import com.testerum.common.serializing.BaseSerializer
import com.testerum.test_file_format.common.step_call.`var`.FileStepVarSerializer
import com.testerum.test_file_format.common.step_call.part.FileStepCallPartSerializer
import com.testerum.test_file_format.common.step_call.phase.FileStepPhaseSerializer
import java.io.Writer

open class FileStepCallSerializerWithPrefix(private val prefix: String): BaseSerializer<FileStepCall>() {

    override fun serialize(source: FileStepCall, destination: Writer, indentLevel: Int) {
        indent(destination, indentLevel)
        serializeStepCallLine(source, destination, indentLevel)
        serializeVariables(source, destination, indentLevel)
    }

    private fun serializeStepCallLine(source: FileStepCall, destination: Writer, indentLevel: Int) {
        destination.write("$prefix: ")
        FileStepPhaseSerializer.serialize(source.phase, destination, indentLevel)
        destination.write(" ")

        for (part in source.parts) {
            FileStepCallPartSerializer.serialize(part, destination, indentLevel)
        }

        destination.write("\n")
    }

    private fun serializeVariables(source: FileStepCall, destination: Writer, indentLevel: Int) {
        for (variable in source.vars) {
            FileStepVarSerializer.serialize(variable, destination, indentLevel + 1)
            destination.write("\n")
        }
    }

}