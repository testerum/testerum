package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readObject
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeObject
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.parts.param_meta.MapTypeMeta

object MapTypeMetaMarshaller : FastMarshaller<MapTypeMeta> {

    const val TYPE = "MAP"

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: MapTypeMeta, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeString("$prefix.javaType", data.javaType)
        output.writeObject("$prefix.keyType", data.keyType, TypeMetaMarshaller)
        output.writeObject("$prefix.valueType", data.valueType, TypeMetaMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): MapTypeMeta {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val javaType = input.readRequiredString("$prefix.javaType")
        val keyType = input.readObject("$prefix.keyType", TypeMetaMarshaller)
        val valueType = input.readObject("$prefix.valueType", TypeMetaMarshaller)

        return MapTypeMeta(javaType, keyType, valueType)
    }

}
