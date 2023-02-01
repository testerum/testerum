package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.parts.TextStepPatternPart

object TextStepPatternPartMarshaller : FastMarshaller<TextStepPatternPart> {

    const val TYPE = "TEXT"

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: TextStepPatternPart, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeString("$prefix.text", data.text)
    }

    override fun parse(prefix: String, input: FastInput): TextStepPatternPart {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val text = input.readRequiredString("$prefix.text")

        return TextStepPatternPart(text)
    }

}
