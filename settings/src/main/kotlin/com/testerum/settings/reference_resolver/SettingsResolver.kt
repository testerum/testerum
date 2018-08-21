package com.testerum.settings.reference_resolver

import com.testerum.settings.reference_parser.SettingValueReferenceParser
import com.testerum.settings.reference_parser.model.FixedValuePart
import com.testerum.settings.reference_parser.model.ReferenceValuePart
import com.testerum.settings.reference_parser.model.ValuePart

object SettingsResolver {

    fun resolve(valuesByKey: Map<String, String>): Map<String, String> {
        val resolvedValuesByKey = HashMap<String, String>()

        for (key in valuesByKey.keys) {
            resolve(resolvedValuesByKey, key, valuesByKey, keysInResolving = LinkedHashSet())
        }

        return resolvedValuesByKey.sortByKeys(valuesByKey.keys)
    }

    private fun resolve(resolvedValuesByKey: HashMap<String, String>,
                        key: String,
                        valuesByKey: Map<String, String>,
                        keysInResolving: LinkedHashSet<String>) {
        if (key in keysInResolving) {
            throw CyclicReferenceException("the following keys refer to each other, forming a cycle: $keysInResolving")
        }

        val resolvedValue = resolvedValuesByKey[key]
        if (resolvedValue != null) {
            // already resolved
            return
        }

        keysInResolving += key


        val value = valuesByKey[key]
        if (value == null) {
            // unresolved reference
            resolvedValuesByKey[key] = "{{$key}}"
        } else {
            val resolvedValueBuilder = StringBuilder()

            val parts: List<ValuePart> = SettingValueReferenceParser.parse(value)
            for (part in parts) {
                when (part) {
                    is FixedValuePart -> resolvedValueBuilder.append(part.text)
                    is ReferenceValuePart -> {
                        resolve(resolvedValuesByKey, part.key, valuesByKey, keysInResolving)

                        resolvedValueBuilder.append(resolvedValuesByKey[part.key])
                    }
                }
            }

            resolvedValuesByKey[key] = resolvedValueBuilder.toString()
        }

        keysInResolving -= key
    }

    private fun Map<String, String>.sortByKeys(keys: Iterable<String>): Map<String, String> {
        val result = LinkedHashMap<String, String>()

        for (key in keys) {
            result[key] = this[key]!!
        }

        return result
    }

}
