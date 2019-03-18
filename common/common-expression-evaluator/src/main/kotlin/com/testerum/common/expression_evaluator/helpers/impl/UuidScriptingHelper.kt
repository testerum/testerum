package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.util.JsFunction
import com.testerum.common.expression_evaluator.helpers.util.ScriptingArgs
import java.util.UUID

object UuidScriptingHelper : ScriptingHelper {

    private val uuid = object : JsFunction(functionName = "uuid") {
        override fun call(thiz: Any?, args: ScriptingArgs): Any? {
            return UUID.randomUUID().toString()
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
            uuid.functionName to uuid
    )

}
