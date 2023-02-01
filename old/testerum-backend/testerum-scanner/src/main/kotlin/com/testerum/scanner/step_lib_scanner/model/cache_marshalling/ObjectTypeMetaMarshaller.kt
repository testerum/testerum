package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readList
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeList
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.parts.param_meta.ObjectTypeMeta

object ObjectTypeMetaMarshaller : FastMarshaller<ObjectTypeMeta> {

    const val TYPE = "OBJECT"

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: ObjectTypeMeta, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeString("$prefix.javaType", data.javaType)
        output.writeList("$prefix.fields", data.fields, TypeMetaFieldDescriptorMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): ObjectTypeMeta {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val javaType = input.readRequiredString("$prefix.javaType")
        val fields = input.readList("$prefix.fields", TypeMetaFieldDescriptorMarshaller)

        return ObjectTypeMeta(javaType, fields)
    }

}
