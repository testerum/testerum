package net.qutester.service.resources.rdbms.util

import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig

fun RdbmsConnectionConfig.resolveConnectionUrl(): String {
    var result = driverUrlPattern

    result = replaceOptional(result, "HOST", host)
    result = replaceRequired(result, "HOST", host)

    result = replaceOptional(result, "PORT", ""+ port)
    result = replaceRequired(result, "PORT", ""+ port)

    result = replaceOptional(result, "DB", database)
    result = replaceRequired(result, "DB", database)

    return result
}

private fun replaceOptional(pattern: String, variable:String, variableValue: String?): String {
    val optionalRegex = Regex("""\[([^]]*)\{\Q$variable\E\}([^]]*)\]""")

    return optionalRegex.replace(pattern) { matchResult ->
        if (variableValue == null || variableValue.isBlank()) {
            return@replace ""
        } else {
            val before: MatchGroup = matchResult.groups[1]!!
            val after: MatchGroup = matchResult.groups[2]!!

            return@replace before.value + variableValue + after.value
        }
    }
}
private fun replaceRequired(pattern: String, variable:String, variableValue: String?): String {
    val requiredRegex = Regex("""\{\Q$variable\E\}""")

    return requiredRegex.replace(pattern, variableValue?:"")
}
