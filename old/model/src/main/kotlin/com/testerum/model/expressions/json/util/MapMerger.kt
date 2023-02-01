package com.testerum.model.expressions.json.util

import java.util.*

object MapMerger {

    private val DELETE_MARKER_VALUE = "@delete()"

    fun override(base: Map<String, Any?>,
                 overrides: Map<String, Any?>): LinkedHashMap<String, Any?> {
        val result = LinkedHashMap<String, Any?>()

        override(result, base, overrides)

        return result
    }

    private fun override(result: LinkedHashMap<String, Any?>,
                         base: Map<String, Any?>,
                         overrides: Map<String, Any?>) {
        for ((baseKey, baseValue) in base) {
            val overrideValue = overrides[baseKey]
            if (overrideValue == null) {
                result[baseKey] = baseValue
                continue
            }

            // the following WILL fail at runtime if any map is not a Map<String, Any?>,
            // but there is no way around it because of type erasure :(
            @Suppress("UNCHECKED_CAST")
            if (baseValue is Map<*, *> && overrideValue is Map<*, *>) {
                result[baseKey] = override(
                        base = baseValue as Map<String, Any?>,
                        overrides = overrideValue as Map<String, Any?>
                )
            } else {
                if (overrideValue == "\\$DELETE_MARKER_VALUE") {
                    result[baseKey] = DELETE_MARKER_VALUE
                } else if (overrideValue != DELETE_MARKER_VALUE) {
                    // we only add the override value if it's not the DELETE_MARKER_VALUE
                    result[baseKey] = overrideValue
                }

            }
        }

        for ((overrideKey, overrideValue) in overrides) {
            if (!base.containsKey(overrideKey) && overrideValue != DELETE_MARKER_VALUE) {
                result[overrideKey] = overrideValue
            }
        }
    }

}
