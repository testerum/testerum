package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ContainerNode
import com.fasterxml.jackson.databind.node.DecimalNode
import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common.json_diff_util.areNumericNodesEqual
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.functions.AssertionFunction
import java.math.BigDecimal

object TypeCastFunctions {

    @AssertionFunction
    fun isTrue(actualNode: JsonNode) {
        matchesBoolean(actualNode, true)
    }

    @AssertionFunction
    fun isFalse(actualNode: JsonNode) {
        matchesBoolean(actualNode, false)
    }

    private fun matchesBoolean(actualNode: JsonNode, expected: Boolean) {
        if (actualNode is NullNode) {
            throw AssertionFailedException("expected a $expected boolean value, but got null instead")
        }
        if (actualNode is ContainerNode<*>) {
            throw AssertionFailedException("expected a $expected boolean value, but got a node of type [${actualNode.javaClass}] instead")
        }

        verifyText(actualNode, "expected a $expected boolean value", { it == expected.toString() })
    }

    @AssertionFunction
    fun matchesNumber(actualNode: JsonNode, expected: BigDecimal?) {
        if (expected == null) {
            if (actualNode !is NullNode) {
                throw AssertionFailedException("expected null, but got a node of type [${actualNode.javaClass}] instead")
            } else {
                // all ok
                return
            }
        }

        val expectedNode = DecimalNode(expected)

        if (!areNumericNodesEqual(expectedNode, actualNode)) {
            throw AssertionFailedException("mismatched values: expected [${expectedNode.numberValue()}], but got [${actualNode.numberValue()}] instead")
        }
    }

}