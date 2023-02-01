package com.testerum.model.resources.http.response.xml_body.functions.impl

import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs
import com.testerum.model.resources.http.response.xml_body.utils.getChildrenByNameAsXmlJsObjects
import org.jdom2.Element

class GetChildXmlJsFunction(private val element: Element) : JsFunction() {

    companion object {
        const val FUNCTION_NAME = "getChild"
    }

    override val name: String
        get() = FUNCTION_NAME

    override fun call(receiver: Any?, args: ScriptingArgs): Any {
        args.requireLength(1)
        val childName: String = args[0]

        return element.getChildrenByNameAsXmlJsObjects(childName)
    }
}
