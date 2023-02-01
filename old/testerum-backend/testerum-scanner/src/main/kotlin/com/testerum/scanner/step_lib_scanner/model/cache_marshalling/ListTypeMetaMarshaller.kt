package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readObject
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeObject
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.parts.param_meta.ListTypeMeta

object ListTypeMetaMarshaller : FastMarshaller<ListTypeMeta> {

    const val TYPE = "LIST"

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: ListTypeMeta, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeString("$prefix.javaType", data.javaType)
        output.writeObject("$prefix.itemsType", data.itemsType, TypeMetaMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): ListTypeMeta {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val javaType = input.readRequiredString("$prefix.javaType")
        val itemsType = input.readObject("$prefix.itemsType", TypeMetaMarshaller)

        return ListTypeMeta(javaType, itemsType)
    }

}
