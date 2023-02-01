package com.testerum.common.expression_evaluator.helpers.impl

import com.mifmif.common.regex.Generex
import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs

/**
 * String generator based on a regex expression.
 * @see <a href="https://github.com/mifmif/Generex">https://github.com/mifmif/Generex</a>
 */
object GenerateStringByRegexScriptingHelper : ScriptingHelper {

    private object GenerateStringByRegexJsFunction : JsFunction() {
        override val name: String
            get() = "generateStringByRegex"

        override fun call(receiver: Any?, args: ScriptingArgs): Any? {
            args.requireLength(1)
            val regex: String = args[0]

            return Generex(regex).random()
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
        GenerateStringByRegexJsFunction.name to GenerateStringByRegexJsFunction
    )

}
