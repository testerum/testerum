package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.functions.AssertionFunction
import java.math.BigDecimal

object ArrayFunctions {

    @AssertionFunction
    fun isArray(actualNode: JsonNode) {
        if (actualNode is ArrayNode) {
            return
        }

        throw AssertionFailedException("expected an Array, but got ${actualNode.toString()}")
    }
    
    @AssertionFunction
    fun isArrayOrNull(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            return
        }

        if (actualNode is ArrayNode) {
            return
        }

        throw AssertionFailedException("expected an Array or null, but got ${actualNode.toString()}")
    }

    @AssertionFunction
    fun isNonEmptyArray(actualNode: JsonNode) {
        if (actualNode is ArrayNode) {
            val arrayNode = actualNode.get(0)
            if (arrayNode != null) {
                val actualSize = arrayNode.size()
                if (actualSize > 0) {
                    return
                } else {
                    throw AssertionFailedException("expected a non empty Array, but got an Array with ${actualSize} ${getElementsWord(actualSize)}: ${arrayNode.toString()}")
                }
            }
        }

        throw AssertionFailedException("expected a non empty Array, but got ${actualNode.toString()}")
    }

    @AssertionFunction
    fun isArrayWithSize(actualNode: JsonNode, expectedValue: BigDecimal) {
        if (actualNode is ArrayNode) {
            val expectedSize = expectedValue.intValueExact()
            val actualSize = actualNode.size()
            if (actualSize == expectedSize) {
                return
            } else {
                throw AssertionFailedException("expected an Array with $expectedSize ${getElementsWord(expectedSize)}, but got an Array with $actualSize ${getElementsWord(actualSize)}: ${actualNode.toString()}")
            }
        }

        throw AssertionFailedException("expected an Array, but got ${actualNode.toString()}")
    }

    @AssertionFunction
    fun isArrayWithSizeGreaterThan(actualNode: JsonNode, expectedValue: BigDecimal) {
        if (actualNode is ArrayNode) {
            val expectedSize = expectedValue.intValueExact()
            val actualSize = actualNode.size()
            if(actualSize > expectedSize) {
                return
            } else {
                throw AssertionFailedException("expected an Array with more than $expectedSize ${getElementsWord(expectedSize)}, but got an Array with $actualSize ${getElementsWord(actualSize)}: ${actualNode.toString()}")
            }
        }

        throw AssertionFailedException("expected an Array, but got ${actualNode.toString()}")
    }

    @AssertionFunction
    fun isArrayWithSizeLessThan(actualNode: JsonNode, expectedValue: BigDecimal) {
        if (actualNode is ArrayNode) {
            val expectedSize = expectedValue.intValueExact()
            val actualSize = actualNode.size()
            if(actualSize < expectedSize) {
                return
            } else {
                throw AssertionFailedException("expected an Array with less than $expectedSize ${getElementsWord(expectedSize)}, but got an Array with $actualSize ${getElementsWord(actualSize)}: ${actualNode.toString()}")
            }
        }

        throw AssertionFailedException("expected an Array, but got ${actualNode.toString()}")
    }

    private fun getElementsWord(size: Int): String {
        return if(size == 1) "element" else "elements"
    }
}
