package com.testerum.common_fast_serialization.marshallers

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

object ByteArrayFastMarshaller : FastMarshaller<ByteArray> {

    override fun serialize(prefix: String, data: ByteArray, output: FastOutput) {
        output.writeByteArray(prefix, data)
    }

    override fun parse(prefix: String, input: FastInput): ByteArray {
        return input.readRequiredByteArray(prefix)
    }

}
