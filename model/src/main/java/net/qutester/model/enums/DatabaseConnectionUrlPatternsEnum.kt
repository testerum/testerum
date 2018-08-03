package net.qutester.model.enums

enum class DatabaseConnectionUrlPatternsEnum(private val variable: String) {

    HOST("HOST"),
    PORT("PORT"),
    DB("DB");

    private val optionalRegex = Regex("""\[([^]]*)\{\Q${variable}\E\}([^]]*)\]""")
    private val requiredRegex = Regex("""\{\Q${variable}\E\}""")

    fun replace(pattern: String, variableValue: String): String {
        var result = pattern

        result = replaceOptional(result, variableValue)
        result = replaceRequired(result, variableValue)

        return result
    }

    private fun replaceOptional(pattern: String, variableValue: String): String {
        return optionalRegex.replace(pattern) { matchResult ->
            if (variableValue.isBlank()) {
                return@replace ""
            } else {
                val before: MatchGroup = matchResult.groups[1]!!
                val after: MatchGroup = matchResult.groups[2]!!

                return@replace before.value + variableValue + after.value
            }
        }
    }
    private fun replaceRequired(pattern: String, variableValue: String): String {
        return requiredRegex.replace(pattern, variableValue)
    }

}