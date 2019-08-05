package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.util.JsFunction
import com.testerum.common.expression_evaluator.helpers.util.ScriptingArgs

object TestHelper : ScriptingHelper {

    private val failTestFunction = object : JsFunction(functionName = "failTest") {
        override fun call(thiz: Any?, args: ScriptingArgs): Any? {
            args.requireLength(1)
            val errorMessage: String = args[0]

            throw AssertionError(errorMessage)
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
            failTestFunction.functionName to failTestFunction
    )

}
