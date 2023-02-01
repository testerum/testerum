package com.testerum.common_fast_serialization.marshallers

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

object StringFastMarshaller : FastMarshaller<String> {

    override fun serialize(prefix: String, data: String, output: FastOutput) {
        output.writeString(prefix, data)
    }

    override fun parse(prefix: String, input: FastInput): String {
        return input.readRequiredString(prefix)
    }

}
