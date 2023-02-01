package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.util.TextTransformJsFunction
import org.apache.commons.text.StringEscapeUtils

object EscapeHelper : ScriptingHelper {

    private val escapeJson = TextTransformJsFunction(name = "escapeJson") {
        StringEscapeUtils.escapeJson(it)
    }

    private val unescapeJson = TextTransformJsFunction(name = "unescapeJson") {
        StringEscapeUtils.unescapeJson(it)
    }

    private val escapeXml = TextTransformJsFunction(name = "escapeXml") {
        StringEscapeUtils.escapeXml10(it)
    }

    private val escapeXml11 = TextTransformJsFunction(name = "escapeXml11") {
        StringEscapeUtils.escapeXml11(it)
    }

    private val unescapeXml = TextTransformJsFunction(name = "unescapeXml") {
        StringEscapeUtils.unescapeXml(it)
    }

    private val escapeJavaScript = TextTransformJsFunction(name = "escapeJavaScript") {
        StringEscapeUtils.escapeEcmaScript(it)
    }

    private val unescapeJavaScript = TextTransformJsFunction(name = "unescapeJavaScript") {
        StringEscapeUtils.unescapeEcmaScript(it)
    }

    private val escapeHtml = TextTransformJsFunction(name = "escapeHtml") {
        StringEscapeUtils.escapeHtml4(it)
    }

    private val unescapeHtml = TextTransformJsFunction(name = "unescapeHtml") {
        StringEscapeUtils.unescapeHtml4(it)
    }

    override val globalVariables: Map<String, Any?> = mapOf(
        escapeJson.name to escapeJson,
        unescapeJson.name to unescapeJson,

        escapeXml.name to escapeXml,
        escapeXml11.name to escapeXml11,
        unescapeXml.name to unescapeXml,

        escapeJavaScript.name to escapeJavaScript,
        unescapeJavaScript.name to unescapeJavaScript,

        escapeHtml.name to escapeHtml,
        unescapeHtml.name to unescapeHtml
    )

}
