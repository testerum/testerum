package com.testerum.common.expression_evaluator.helpers.impl

import com.mifmif.common.regex.Generex
import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.util.JsFunction
import com.testerum.common.expression_evaluator.helpers.util.ScriptingArgs

/**
 * String generator based on a regex expression.
 * @see <a href="https://github.com/mifmif/Generex">https://github.com/mifmif/Generex</a>
 */
object GenerateStringByRegexScriptingHelper : ScriptingHelper {

    private val generateStringByRegex = object : JsFunction(functionName = "generateStringByRegex") {
        override fun call(thiz: Any?, args: ScriptingArgs): Any? {
            args.requireMinimumLength(minLength = 1)
            val regex: String = args[0]

            return Generex(regex).random()
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
            generateStringByRegex.functionName to generateStringByRegex
    )

}
