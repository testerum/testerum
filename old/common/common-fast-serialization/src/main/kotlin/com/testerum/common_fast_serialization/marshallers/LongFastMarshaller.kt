package com.testerum.common_fast_serialization.marshallers

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

object LongFastMarshaller : FastMarshaller<Long> {

    override fun serialize(prefix: String, data: Long, output: FastOutput) {
        output.writeLong(prefix, data)
    }

    override fun parse(prefix: String, input: FastInput): Long {
        return input.readRequiredLong(prefix)
    }

}
