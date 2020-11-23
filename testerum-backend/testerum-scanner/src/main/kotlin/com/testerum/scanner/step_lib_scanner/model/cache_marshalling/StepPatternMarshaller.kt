package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readList
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeList
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.StepPattern

object StepPatternMarshaller : FastMarshaller<StepPattern> {

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: StepPattern, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeList("$prefix.patternParts", data.patternParts, StepPatternPartMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): StepPattern {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val patternParts = input.readList("$prefix.patternParts", StepPatternPartMarshaller)

        return StepPattern(patternParts)
    }

}
