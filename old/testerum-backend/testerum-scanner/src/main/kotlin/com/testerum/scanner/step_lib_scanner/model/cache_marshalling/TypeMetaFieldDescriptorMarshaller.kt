package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readObject
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeObject
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.parts.param_meta.field.TypeMetaFieldDescriptor

object TypeMetaFieldDescriptorMarshaller : FastMarshaller<TypeMetaFieldDescriptor> {

    const val TYPE = "OBJECT"

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: TypeMetaFieldDescriptor, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeString("$prefix.name", data.name)
        output.writeObject("$prefix.type", data.type, TypeMetaMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): TypeMetaFieldDescriptor {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val name = input.readRequiredString("$prefix.name")
        val type = input.readObject("$prefix.type", TypeMetaMarshaller)

        return TypeMetaFieldDescriptor(name, type)
    }

}
