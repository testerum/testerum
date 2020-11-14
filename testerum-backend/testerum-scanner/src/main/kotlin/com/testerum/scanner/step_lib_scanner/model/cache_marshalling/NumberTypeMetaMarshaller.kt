package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.parts.param_meta.NumberTypeMeta

object NumberTypeMetaMarshaller : FastMarshaller<NumberTypeMeta> {

    const val TYPE = "NUMBER"

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: NumberTypeMeta, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeString("$prefix.javaType", data.javaType)
    }

    override fun parse(prefix: String, input: FastInput): NumberTypeMeta {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val javaType = input.readRequiredString("$prefix.javaType")

        return NumberTypeMeta(javaType)
    }

}
