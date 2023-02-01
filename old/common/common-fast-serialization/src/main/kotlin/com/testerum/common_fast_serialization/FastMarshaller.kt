package com.testerum.common_fast_serialization

import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

interface FastMarshaller<T> {

    fun serialize(prefix: String, data: T, output: FastOutput)

    fun parse(prefix: String, input: FastInput): T

}
