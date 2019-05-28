package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.util.TextTranslationJsFunction
import org.apache.commons.text.StringEscapeUtils

object EscapeHelper : ScriptingHelper {

    private val escapeJson = TextTranslationJsFunction(functionName = "escapeJson") {
        StringEscapeUtils.escapeJson(it)
    }

    private val unescapeJson = TextTranslationJsFunction(functionName = "unescapeJson") {
        StringEscapeUtils.unescapeJson(it)
    }

    private val escapeXml = TextTranslationJsFunction(functionName = "escapeXml") {
        StringEscapeUtils.escapeXml10(it)
    }

    private val escapeXml11 = TextTranslationJsFunction(functionName = "escapeXml11") {
        StringEscapeUtils.escapeXml11(it)
    }

    private val unescapeXml = TextTranslationJsFunction(functionName = "unescapeXml") {
        StringEscapeUtils.unescapeXml(it)
    }

    private val escapeJavaScript = TextTranslationJsFunction(functionName = "escapeJavaScript") {
        StringEscapeUtils.escapeEcmaScript(it)
    }

    private val unescapeJavaScript = TextTranslationJsFunction(functionName = "unescapeJavaScript") {
        StringEscapeUtils.unescapeEcmaScript(it)
    }

    private val escapeHtml = TextTranslationJsFunction(functionName = "escapeHtml") {
        StringEscapeUtils.escapeHtml4(it)
    }

    private val unescapeHtml = TextTranslationJsFunction(functionName = "unescapeHtml") {
        StringEscapeUtils.unescapeHtml4(it)
    }

    override val globalVariables: Map<String, Any?> = mapOf(
            escapeJson.functionName          to escapeJson,
            unescapeJson.functionName        to unescapeJson,

            escapeXml.functionName           to escapeXml,
            escapeXml11.functionName         to escapeXml11,
            unescapeXml.functionName         to unescapeXml,

            escapeJavaScript.functionName    to escapeJavaScript,
            unescapeJavaScript.functionName  to unescapeJavaScript,

            escapeHtml.functionName          to escapeHtml,
            unescapeHtml.functionName        to unescapeHtml
    )

}
