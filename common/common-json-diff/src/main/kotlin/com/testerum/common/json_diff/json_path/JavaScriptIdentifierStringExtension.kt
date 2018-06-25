package com.testerum.common.json_diff.json_path

internal fun String.isValidJavascriptIdentifier(): Boolean {
    if (this.isEmpty()) {
        return false
    }
    if (this.isJavascriptReservedWord()) {
        return false
    }

    // assuming same rules as in Java
    if (!Character.isJavaIdentifierStart(this[0])) {
        return false
    }
    for (i in 1 until this.length) {
        if (!Character.isJavaIdentifierPart(this[i])) {
            return false
        }
    }

    return true
}

private val JAVASCRIPT_RESERVED_WORDS = setOf(
        "instanceof", "typeof", "break", "do", "new", "var", "case", "else", "return", "void", "catch", "finally",
        "continue", "for", "switch", "while", "this", "with", "debugger", "function", "throw", "default", "if",
        "try", "delete", "in"
)
private fun String.isJavascriptReservedWord(): Boolean = JAVASCRIPT_RESERVED_WORDS.contains(this)
