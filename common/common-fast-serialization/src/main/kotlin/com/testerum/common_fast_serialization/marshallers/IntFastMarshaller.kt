package com.testerum.common_fast_serialization.marshallers

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

object IntFastMarshaller : FastMarshaller<Int> {

    override fun serialize(prefix: String, data: Int, output: FastOutput) {
        output.writeInt(prefix, data)
    }

    override fun parse(prefix: String, input: FastInput): Int {
        return input.readRequiredInt(prefix)
    }

}
