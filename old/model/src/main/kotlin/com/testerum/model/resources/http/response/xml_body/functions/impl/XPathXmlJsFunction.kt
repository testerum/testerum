package com.testerum.model.resources.http.response.xml_body.functions.impl

import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs
import org.jdom2.Attribute
import org.jdom2.Content
import org.jdom2.Element
import org.jdom2.Text
import org.jdom2.xpath.XPathFactory

class XPathXmlJsFunction(private val element: Element) : JsFunction() {

    companion object {
        const val FUNCTION_NAME = "xpath"
    }

    override val name: String
        get() = FUNCTION_NAME

    override fun call(receiver: Any?, args: ScriptingArgs): Any? {
        args.requireLength(1)
        val xpathExpression: String = args[0]

        val xpathFactory: XPathFactory = XPathFactory.instance()

        val xPathResults = xpathFactory.compile(xpathExpression).evaluate(element)

        val results = xPathResults.map {
            when (it) {
                is Attribute -> it.value
                is Text -> it.textTrim
                is Content -> it.value
                else -> it.toString()
            }
        }

        if (results.isEmpty()) {
            return null
        }
        if (results.size == 1) {
            return results.first()
        }

        return results
    }
}
