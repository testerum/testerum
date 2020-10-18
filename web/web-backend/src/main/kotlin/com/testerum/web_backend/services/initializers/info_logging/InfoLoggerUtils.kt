package com.testerum.web_backend.services.initializers.info_logging

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger

object InfoLoggerUtils {

    fun logMap(logger: Logger,
               map: Map<String, Any?>,
               prefix: String = "* ") {
        if (!logger.isInfoEnabled) {
            return
        }

        val mapWithStringValues = map.mapValues { it.value?.toString().orEmpty() }

        val sizeOfBiggestKey = findSizeOfBiggestKey(mapWithStringValues) + 2

        mapWithStringValues.forEach { key, value ->
            val keyToDisplay = StringUtils.rightPad("[$key]", sizeOfBiggestKey)
            val valueToDisplay = getValueToDisplay(key, value)

            logger.info("{}{} = [{}]", prefix, keyToDisplay, valueToDisplay)
        }
    }

    private fun findSizeOfBiggestKey(map: Map<String, String>): Int {
        return map.keys
                .asSequence()
                .map { it.length }
                .maxOrNull() ?: 0
    }

    private fun getValueToDisplay(key: String?,
                                  value: String): String {
        return if (key != null && key.toLowerCase().contains("pass")) {
            "****************************************"
        } else {
            value
        }
    }
}
