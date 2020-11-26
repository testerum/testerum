package com.testerum.common_fast_serialization.marshallers

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

object CharFastMarshaller : FastMarshaller<Char> {

    override fun serialize(prefix: String, data: Char, output: FastOutput) {
        output.writeChar(prefix, data)
    }

    override fun parse(prefix: String, input: FastInput): Char {
        return input.readRequiredChar(prefix)
    }

}
