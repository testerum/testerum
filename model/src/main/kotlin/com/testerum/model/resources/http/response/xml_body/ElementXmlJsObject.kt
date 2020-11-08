package com.testerum.model.resources.http.response.xml_body

import com.testerum.model.resources.http.response.xml_body.functions.impl.GetAttrXmlJsFunction
import com.testerum.model.resources.http.response.xml_body.functions.impl.GetChildXmlJsFunction
import com.testerum.model.resources.http.response.xml_body.functions.impl.GetTextXmlJsFunction
import com.testerum.model.resources.http.response.xml_body.functions.impl.XPathXmlJsFunction
import com.testerum.model.resources.http.response.xml_body.utils.getChildrenByNameAsXmlJsObjects
import jdk.nashorn.api.scripting.AbstractJSObject
import org.jdom2.Element

class ElementXmlJsObject(private val element: Element) : AbstractJSObject() {

    companion object {
        private val XML_FUNCTION_FACTORIES = mapOf<String, XmlFunctionFactory>(
            GetAttrXmlJsFunction.FUNCTION_NAME to { element -> GetAttrXmlJsFunction(element) },
            GetTextXmlJsFunction.FUNCTION_NAME to { element -> GetTextXmlJsFunction(element) },
            GetChildXmlJsFunction.FUNCTION_NAME to { element -> GetChildXmlJsFunction(element) },
            XPathXmlJsFunction.FUNCTION_NAME to { element -> XPathXmlJsFunction(element) },
        )
    }

    override fun hasMember(name: String): Boolean {
        if (XML_FUNCTION_FACTORIES.containsKey(name)) {
            return true
        }
        if (element.getChild(name) != null) {
            return true
        }

        return false
    }

    override fun getMember(name: String): Any {
        val createFunction = XML_FUNCTION_FACTORIES[name]
        if (createFunction != null) {
            return createFunction(element)
        }

        return element.getChildrenByNameAsXmlJsObjects(name)
    }

    override fun setMember(name: String?, value: Any?) {
        throw UnsupportedOperationException("it is not allowed to modify the XML document")
    }

    override fun removeMember(name: String?) {
        throw UnsupportedOperationException("it is not allowed to modify the XML document")
    }
}

