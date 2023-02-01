package com.testerum.common_fast_serialization.marshallers

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

object FloatFastMarshaller : FastMarshaller<Float> {

    override fun serialize(prefix: String, data: Float, output: FastOutput) {
        output.writeFloat(prefix, data)
    }

    override fun parse(prefix: String, input: FastInput): Float {
        return input.readRequiredFloat(prefix)
    }

}
