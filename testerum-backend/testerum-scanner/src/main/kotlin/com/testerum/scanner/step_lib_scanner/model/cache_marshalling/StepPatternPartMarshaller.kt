package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readObject
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeObject
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.common_kotlin.exhaustive
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart

object StepPatternPartMarshaller : FastMarshaller<StepPatternPart> {

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: StepPatternPart, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        when (data) {
            is ParamStepPatternPart -> {
                output.writeString("$prefix.@type", ParamStepPatternPartMarshaller.TYPE)
                output.writeObject(prefix, data, ParamStepPatternPartMarshaller)
            }
            is TextStepPatternPart -> {
                output.writeString("$prefix.@type", TextStepPatternPartMarshaller.TYPE)
                output.writeObject(prefix, data, TextStepPatternPartMarshaller)
            }
        }.exhaustive()

    }

    override fun parse(prefix: String, input: FastInput): StepPatternPart {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        return when (val type = input.readRequiredString("$prefix.@type")) {
            ParamStepPatternPartMarshaller.TYPE -> input.readObject(prefix, ParamStepPatternPartMarshaller)
            TextStepPatternPartMarshaller.TYPE -> input.readObject(prefix, TextStepPatternPartMarshaller)
            else -> throw RuntimeException("unknown type [$type]")
        }
    }

}
