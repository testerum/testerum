package json.utils

import java.util.*

object MapMerger {

    fun override(base: Map<String, Any?>,
                 overrides: Map<String, Any?>): TreeMap<String, Any?> {
        val result = TreeMap<String, Any?>()

        override(result, base, overrides)

        return result
    }

    private fun override(result: TreeMap<String, Any?>,
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
                result[baseKey] = overrideValue
            }
        }

        for ((overrideKey, overrideValue) in overrides) {
            if (!base.containsKey(overrideKey)) {
                result[overrideKey] = overrideValue
            }
        }
    }

}
