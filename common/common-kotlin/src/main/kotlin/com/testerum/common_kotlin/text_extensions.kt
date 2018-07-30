package com.testerum.common_kotlin

fun StringBuilder.indent(indent: Int, indentPerLevel: Int = 4): StringBuilder {
    for (i in 1..indent) {
        append(" ".repeat(indentPerLevel))
    }

    return this
}

fun String.emptyToNull(): String? = if (this == "") null else this
// the counterpart of "emptyToNull()" is "orEmpty()" found in the standard library