package com.testerum.common_fast_serialization.marshallers

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

object DoubleFastMarshaller : FastMarshaller<Double> {

    override fun serialize(prefix: String, data: Double, output: FastOutput) {
        output.writeDouble(prefix, data)
    }

    override fun parse(prefix: String, input: FastInput): Double {
        return input.readRequiredDouble(prefix)
    }

}
