package com.testerum.runner.cmdline

object OutputFormatParser {

    private val PROPERTIES_SPLITTER = Regex("""(?<!\\),""")
    private val PROPERTY_PAIR_SPLITTER = Regex("""(?<!\\)=""")

    fun parse(outputFormatWithProperties: String): Pair<OutputFormat, Map<String, String>> {
        val indexOfColon = outputFormatWithProperties.indexOf(":")

        val outputFormatString: String
        val propertiesString: String

        if (indexOfColon == -1) {
            outputFormatString = outputFormatWithProperties
            propertiesString = ""
        } else {
            outputFormatString = outputFormatWithProperties.substring(0, indexOfColon)
            propertiesString = outputFormatWithProperties.substring(indexOfColon + 1)
        }

        val outputFormat = OutputFormat.parse(outputFormatString)
        val properties = parseProperties(propertiesString)

        return Pair(outputFormat, properties)
    }

    private fun parseProperties(propertiesString: String): Map<String, String> {
        if (propertiesString.isEmpty()) {
            return emptyMap()
        }

        val result = LinkedHashMap<String, String>()

        val propertyPairs = PROPERTIES_SPLITTER.split(propertiesString)
        for (propertyPair in propertyPairs) {
            val propertyPairParts = PROPERTY_PAIR_SPLITTER.split(propertyPair)

            if (propertyPairParts.size == 1) {
                val key = propertyPairParts[0].unescape()

                result[key] = ""
            } else if (propertyPairParts.size == 2) {
                val key = propertyPairParts[0].unescape()
                val value = propertyPairParts[1].unescape()

                result[key] = value
            } else {
                throw IllegalArgumentException(
                        "[$propertiesString] has invalid format" +
                                ": the pair [$propertyPair] should contain an '=' character that separates the key and the value"
                )
            }

        }

        return result
    }

    private fun String.unescape(): String {
        return this.replace("\\=", "=")
                .replace("\\,", ",")
    }
}