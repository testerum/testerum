package com.testerum.scanner.step_lib_scanner.model.cache_marshalling

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.marshallers.StringFastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_fast_serialization.read_write.extensions.readList
import com.testerum.common_fast_serialization.read_write.extensions.readNullable
import com.testerum.common_fast_serialization.read_write.extensions.readRequiredEnum
import com.testerum.common_fast_serialization.read_write.extensions.requireExactVersion
import com.testerum.common_fast_serialization.read_write.extensions.writeEnum
import com.testerum.common_fast_serialization.read_write.extensions.writeList
import com.testerum.common_fast_serialization.read_write.extensions.writeNullable
import com.testerum.common_fast_serialization.read_write.extensions.writeVersion
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingType

object SettingDefinitionMarshaller : FastMarshaller<SettingDefinition> {

    private const val CURRENT_VERSION = 1

    override fun serialize(prefix: String, data: SettingDefinition, output: FastOutput) {
        output.writeVersion(prefix, CURRENT_VERSION)

        output.writeString("$prefix.key", data.key)
        output.writeString("$prefix.label", data.label)
        output.writeEnum("$prefix.type", data.type)
        output.writeString("$prefix.defaultValue", data.defaultValue)
        output.writeList("$prefix.enumValues", data.enumValues, StringFastMarshaller)
        output.writeNullable("$prefix.description", data.description, StringFastMarshaller)
        output.writeNullable("$prefix.category", data.category, StringFastMarshaller)
        output.writeBoolean("$prefix.defined", data.defined)
    }

    override fun parse(prefix: String, input: FastInput): SettingDefinition {
        input.requireExactVersion(prefix, CURRENT_VERSION)

        val key = input.readRequiredString("$prefix.key")
        val label = input.readRequiredString("$prefix.label")
        val type = input.readRequiredEnum<SettingType>("$prefix.type")
        val defaultValue = input.readRequiredString("$prefix.defaultValue")
        val enumValues = input.readList("$prefix.enumValues", StringFastMarshaller)
        val description = input.readNullable("$prefix.description", StringFastMarshaller)
        val category = input.readNullable("$prefix.category", StringFastMarshaller)
        val defined = input.readRequiredBoolean("$prefix.defined")

        return SettingDefinition(key, label, type, defaultValue, enumValues, description, category, defined)
    }

}
