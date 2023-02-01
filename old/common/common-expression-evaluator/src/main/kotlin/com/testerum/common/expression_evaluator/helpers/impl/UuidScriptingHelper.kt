package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs
import java.util.UUID

object UuidScriptingHelper : ScriptingHelper {

    private object UuidJsFunction : JsFunction() {
        override val name: String
            get() = "uuid"

        override fun call(receiver: Any?, args: ScriptingArgs): Any {
            return UUID.randomUUID().toString()
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
        UuidJsFunction.name to UuidJsFunction
    )

}
