package com.testerum.file_service.mapper.util

object ArgNameCodec {

    private val INVALID_VARIABLE_CHARACTER = Regex("[^_0-9a-zA-Z]")

    fun argToVariableName(argName: String): String {
        if (argName.isEmpty()) {
            return "_"
        }

        var varName: String = argName

        varName = varName.replace(INVALID_VARIABLE_CHARACTER, "_")

        return if (argName[0] in '0'..'9') {
            // variables can't start with a digit
            "_$varName"
        } else {
            varName
        }
    }

    fun variableToArgName(varName: String): String {
        if (varName == "_") {
            return ""
        }

        val argName = StringBuilder()

        for (c in varName) {
            val translated: Char = if (c == '_') {
                ' '
            } else {
                c
            }

            argName.append(translated)
        }

        return if (varName.length >= 2
                && varName[0] == '_'
                && varName[1] in '0'..'9') {
            // remove the extra underscore added to make it a valid variable name
            argName.substring(1)
        } else {
            argName.toString()
        }
    }

}
