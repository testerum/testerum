package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.marshallers.StringFastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readList
import com.testerum.common_fast_serialization.read_write.extensions.readNullable
import com.testerum.common_fast_serialization.read_write.extensions.readObject
import com.testerum.common_fast_serialization.read_write.extensions.readRequiredEnum
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeEnum
import com.testerum.common_fast_serialization.read_write.extensions.writeList
import com.testerum.common_fast_serialization.read_write.extensions.writeNullable
import com.testerum.common_fast_serialization.read_write.extensions.writeObject
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.step.BasicStepDef

object BasicStepDefMarshaller : FastMarshaller<BasicStepDef> {

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: BasicStepDef, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeEnum("$prefix.phase", data.phase)
        output.writeObject("$prefix.stepPattern", data.stepPattern, StepPatternMarshaller)
        output.writeNullable("$prefix.description", data.description, StringFastMarshaller)
        output.writeList("$prefix.tags", data.tags, StringFastMarshaller)
        output.writeString("$prefix.className", data.className)
        output.writeString("$prefix.methodName", data.methodName)
        output.writeObject("$prefix.resultType", data.resultType, TypeMetaMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): BasicStepDef {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val phase = input.readRequiredEnum<StepPhaseEnum>("$prefix.phase")
        val stepPattern = input.readObject("$prefix.stepPattern", StepPatternMarshaller)
        val description = input.readNullable("$prefix.description", StringFastMarshaller)
        val tags = input.readList("$prefix.tags", StringFastMarshaller)
        val className = input.readRequiredString("$prefix.className")
        val methodName = input.readRequiredString("$prefix.methodName")
        val resultType = input.readObject("$prefix.resultType", TypeMetaMarshaller)

        return BasicStepDef(phase, stepPattern, description, tags, className, methodName, resultType)
    }

}
