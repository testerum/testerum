package net.qutester.util

private val INDENT = "    "

fun <T: Appendable> T.indent(indentLevel: Int): T {
    for (i in 1..indentLevel) {
        append(INDENT)
    }

    return this
}