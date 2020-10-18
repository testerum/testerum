package com.testerum.model.resources.http.response.xml_body.functions.impl

import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs
import com.testerum.model.resources.http.response.xml_body.utils.getNodePath
import org.jdom2.Content
import org.jdom2.Element

class GetTextXmlJsFunction(private val element: Element) : JsFunction() {

    companion object {
        const val FUNCTION_NAME = "getText"
    }

    override val name: String
        get() = FUNCTION_NAME

    override fun call(receiver: Any?, args: ScriptingArgs): Any? {
        if (args.size == 0) {
            return element.textNormalize
        }

        args.requireLength(1)
        val textNodeIndex: Int = args[0]

        val textNodes = element.content.filter { it.cType == Content.CType.Text }
        if (textNodeIndex !in textNodes.indices) {
            throw RuntimeException(
                "invalid text node index [$textNodeIndex]" +
                    ": must be between 0 and ${textNodes.size - 1} (inclusive)" +
                    "; path=[${getNodePath(element)}]"
            )
        }

        return textNodes[textNodeIndex].value.trim()
    }
}

