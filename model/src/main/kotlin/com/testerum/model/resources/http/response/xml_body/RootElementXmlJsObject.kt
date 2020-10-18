package com.testerum.model.resources.http.response.xml_body

import com.testerum.model.resources.http.response.xml_body.functions.impl.XPathXmlJsFunction
import jdk.nashorn.api.scripting.AbstractJSObject
import jdk.nashorn.api.scripting.JSObject
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.located.LocatedJDOMFactory

typealias XmlFunctionFactory = (element: Element) -> JSObject

class RootElementXmlJsObject(byteArray: ByteArray) : AbstractJSObject() {

    companion object {
        private val XML_FUNCTION_FACTORIES = mapOf<String, XmlFunctionFactory>(
            XPathXmlJsFunction.FUNCTION_NAME to { element -> XPathXmlJsFunction(element) },
        )
    }

    private val rootElement: Element by lazy {
        val saxBuilder = SAXBuilder().apply {
            jdomFactory = LocatedJDOMFactory()
        }

        val document: Document = saxBuilder.build(byteArray.inputStream())

        return@lazy document.rootElement
    }

    override fun hasMember(name: String): Boolean {
        if (XML_FUNCTION_FACTORIES.containsKey(name)) {
            return true
        }

        return rootElement.name == name
    }

    override fun getMember(name: String): Any {
        val createFunction = XML_FUNCTION_FACTORIES[name]
        if (createFunction != null) {
            return createFunction(rootElement)
        }

        if (rootElement.name != name) {
            throw RuntimeException("the only valid root element name is [${rootElement.name}], but got [$name]")
        }

        return ElementXmlJsObject(rootElement)
    }

    override fun setMember(name: String?, value: Any?) {
        throw UnsupportedOperationException("it is not allowed to modify the XML document")
    }

    override fun removeMember(name: String?) {
        throw UnsupportedOperationException("it is not allowed to modify the XML document")
    }

}
