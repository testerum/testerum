package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.marshallers.StringFastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readNullable
import com.testerum.common_fast_serialization.read_write.extensions.readObject
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeNullable
import com.testerum.common_fast_serialization.read_write.extensions.writeObject
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.model.text.parts.ParamStepPatternPart

object ParamStepPatternPartMarshaller : FastMarshaller<ParamStepPatternPart> {

    const val TYPE = "PARAM"

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: ParamStepPatternPart, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeString("$prefix.name", data.name)
        output.writeObject("$prefix.typeMeta", data.typeMeta, TypeMetaMarshaller)
        output.writeNullable("$prefix.description", data.description, StringFastMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): ParamStepPatternPart {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val name = input.readRequiredString("$prefix.name")
        val typeMeta = input.readObject("$prefix.typeMeta", TypeMetaMarshaller)
        val description = input.readNullable("$prefix.description", StringFastMarshaller)

        return ParamStepPatternPart(name, typeMeta, description)
    }

}
