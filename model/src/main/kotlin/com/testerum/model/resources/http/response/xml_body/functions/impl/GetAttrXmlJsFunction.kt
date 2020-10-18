package com.testerum.model.resources.http.response.xml_body.functions.impl

import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs
import com.testerum.model.resources.http.response.xml_body.utils.getNodePath
import org.jdom2.Element

class GetAttrXmlJsFunction(private val element: Element) : JsFunction() {

    companion object {
        const val FUNCTION_NAME = "getAttr"
    }

    override val name: String
        get() = FUNCTION_NAME

    override fun call(receiver: Any?, args: ScriptingArgs): Any? {
        args.requireLength(1)
        val attrName: String = args[0]

        val nodeAttribute = element.getAttribute(attrName)
            ?: throw RuntimeException("no attribute with the name [$attrName] was found; path=[${getNodePath(element)}]")

        return nodeAttribute.value
    }
}
