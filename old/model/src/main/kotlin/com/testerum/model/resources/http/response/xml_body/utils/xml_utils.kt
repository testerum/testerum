package com.testerum.model.resources.http.response.xml_body.utils

import com.testerum.model.resources.http.response.xml_body.ElementXmlJsObject
import org.jdom2.Element
import org.jdom2.located.Located
import org.jdom2.xpath.XPathHelper

fun getNodePath(element: Element): String {
    val location = if (element is Located) {
        "; at line ${element.line}, column ${element.column}"
    } else {
        ""
    }

    return XPathHelper.getAbsolutePath(element) + location
}

fun Element.getChildrenByNameAsXmlJsObjects(childName: String): Any {
    val childElements: List<Element> = this.getChildren(childName)

    return if (childElements.isEmpty()) {
        throw RuntimeException("cannot find any child with the name [$name]; path=[${getNodePath(this)}]")
    } else if (childElements.size == 1) {
        // allow to navigate objects without having to give the index, if there is only one such child
        ElementXmlJsObject(childElements[0])
    } else {
        childElements.map { ElementXmlJsObject(it) }
    }
}
