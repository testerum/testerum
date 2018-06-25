package com.testerum.common_assertion_functions.functions.builtin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ContainerNode
import com.fasterxml.jackson.databind.node.NullNode
import com.testerum.common_assertion_functions.functions.AssertionFailedException
import com.testerum.common_assertion_functions.functions.AssertionFunction

object TextFunctions {

    // "isNull"  not implemented, since one can simply put null as expected value
    // "isEmpty" not implemented, since one can simply put the empty string "" as expected value

    @AssertionFunction
    fun isNotNull(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            throw AssertionFailedException("expected not null, but got null instead")
        }
    }

    @AssertionFunction
    fun isNullOrEmpty(actualNode: JsonNode) {
        verifyText(actualNode, "expected a null or empty value", { it == null || it.isEmpty() })
    }

    @AssertionFunction
    fun isNotEmpty(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            throw AssertionFailedException("expected a non-empty value, but got null instead")
        }
        if (actualNode is ContainerNode<*>) {
            return
        }

        verifyText(actualNode, "expected a non-empty value", { it != null && !it.isEmpty() })
    }

    @AssertionFunction
    fun isBlank(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            throw AssertionFailedException("expected a blank value, but got null instead")
        }
        if (actualNode is ContainerNode<*>) {
            throw AssertionFailedException("expected a blank value, but got a node of type [${actualNode.javaClass}] instead")
        }

        verifyText(actualNode, "expected a blank value", { it != null && it.isBlank() })
    }

    @AssertionFunction
    fun isNullOrBlank(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            return
        }
        if (actualNode is ContainerNode<*>) {
            throw AssertionFailedException("expected a null or blank value, but got a node of type [${actualNode.javaClass}] instead")
        }

        verifyText(actualNode, "expected a null or blank value", { it == null || it.isBlank() })
    }

    @AssertionFunction
    fun isNotBlank(actualNode: JsonNode) {
        if (actualNode is NullNode) {
            return
        }
        if (actualNode is ContainerNode<*>) {
            return
        }

        verifyText(actualNode, "expected a non-blank value", { it != null && !it.isBlank() })
    }

}