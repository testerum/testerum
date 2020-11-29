package com.testerum.common_fast_serialization.marshallers

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

object BooleanFastMarshaller : FastMarshaller<Boolean> {

    override fun serialize(prefix: String, data: Boolean, output: FastOutput) {
        output.writeBoolean(prefix, data)
    }

    override fun parse(prefix: String, input: FastInput): Boolean {
        return input.readRequiredBoolean(prefix)
    }

}
