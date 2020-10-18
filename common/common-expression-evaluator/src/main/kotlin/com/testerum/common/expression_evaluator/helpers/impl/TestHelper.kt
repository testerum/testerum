package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs

object TestHelper : ScriptingHelper {

    private object FailTestJsFunction : JsFunction() {
        override val name: String
            get() = "failTest"

        override fun call(receiver: Any?, args: ScriptingArgs): Any? {
            args.requireLength(1)
            val errorMessage: String = args[0]

            throw AssertionError(errorMessage)
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
        FailTestJsFunction.name to FailTestJsFunction
    )

}
