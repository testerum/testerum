package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.marshallers.StringFastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readNullable
import com.testerum.common_fast_serialization.read_write.extensions.readRequiredEnum
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeEnum
import com.testerum.common_fast_serialization.read_write.extensions.writeNullable
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.model.feature.hooks.HookPhase

object HookDefMarshaller : FastMarshaller<HookDef> {

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: HookDef, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeEnum("$prefix.phase", data.phase)
        output.writeString("$prefix.className", data.className)
        output.writeString("$prefix.methodName", data.methodName)
        output.writeInt("$prefix.order", data.order)
        output.writeNullable("$prefix.description", data.description, StringFastMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): HookDef {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val phase = input.readRequiredEnum<HookPhase>("$prefix.phase")
        val className = input.readRequiredString("$prefix.className")
        val methodName = input.readRequiredString("$prefix.methodName")
        val order = input.readRequiredInt("$prefix.order")
        val description = input.readNullable("$prefix.description", StringFastMarshaller)

        return HookDef(phase, className, methodName, order, description)
    }

}
