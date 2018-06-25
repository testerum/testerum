package com.testerum.common.json_diff.json_path

class JsonPath private constructor(val elementsFromRoot: List<JsonPathElement<*>> = emptyList()) {

    companion object {
        val EMPTY = JsonPath()
    }

    fun push(element: JsonPathElement<*>): JsonPath {
        val newPath = mutableListOf<JsonPathElement<*>>()

        newPath.addAll(elementsFromRoot)
        newPath.add(element)

        return JsonPath(newPath)
    }

    override fun toString() = buildString {
        var first = true
        for (node in elementsFromRoot) {
            if (first) {
                append("$")

                first = false
                continue
            }

            when (node) {
                is JsonNodePathElement -> {
                    if (node.fieldName.isValidJavascriptIdentifier()) {
                        append(".").append(node.fieldName)
                    } else {
                        append("['").append(node.fieldName).append("']")
                    }
                }

                is JsonArrayItemPathElement -> append("[").append(node.index).append("]")
            }
        }
    }

}
