package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readList
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeList
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanResult

object ExtensionsScanResultMarshaller : FastMarshaller<ExtensionsScanResult> {

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: ExtensionsScanResult, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeList("$prefix.steps", data.steps, BasicStepDefMarshaller)
        output.writeList("$prefix.hooks", data.hooks, HookDefMarshaller)
        output.writeList("$prefix.settingDefinitions", data.settingDefinitions, SettingDefinitionMarshaller)
    }

    override fun parse(prefix: String, input: FastInput): ExtensionsScanResult {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val steps = input.readList("$prefix.steps", BasicStepDefMarshaller)
        val hooks = input.readList("$prefix.hooks", HookDefMarshaller)
        val settingDefinitions = input.readList("$prefix.settingDefinitions", SettingDefinitionMarshaller)

        return ExtensionsScanResult(steps, hooks, settingDefinitions)
    }

}
