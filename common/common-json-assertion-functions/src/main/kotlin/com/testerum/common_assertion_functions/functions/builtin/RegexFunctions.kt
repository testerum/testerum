package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ContainerNode
import com.fasterxml.jackson.databind.node.NullNode
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.functions.AssertionFunction

object RegexFunctions {

    private val REGEX_CACHE: LoadingCache<String, Regex> = CacheBuilder.newBuilder()
            .maximumSize(5000)
            .build<String, Regex>(
                    object : CacheLoader<String, Regex>() {
                        override fun load(key: String): Regex {
                            return Regex(key)
                        }
                    }
            )

    @AssertionFunction
    fun matchesRegex(actualNode: JsonNode, patternText: String) {
        val regex = REGEX_CACHE[patternText]

        if (actualNode is NullNode) {
            throw AssertionFailedException("expected a value matching regex '$patternText', but got null instead")
        }
        if (actualNode is ContainerNode<*>) {
            throw AssertionFailedException("expected a value matching regex '$patternText', but got a node of type [${actualNode.javaClass}] instead")
        }

        verifyText(actualNode, "expected a value matching regex '$patternText'", { it != null && regex.matches(it) })
    }

    @AssertionFunction
    fun isNullOrMatchesRegex(actualNode: JsonNode, patternText: String) {
        val regex = REGEX_CACHE[patternText]

        verifyText(actualNode, "expected a null value or a value matching regex '$patternText'", { it == null || regex.matches(it) })
    }

}