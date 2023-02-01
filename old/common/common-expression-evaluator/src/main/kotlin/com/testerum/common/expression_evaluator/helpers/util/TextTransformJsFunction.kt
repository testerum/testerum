package com.testerum.common.expression_evaluator.helpers.util

import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs

class TextTransformJsFunction(
    override val name: String,
    private val transform: (String?) -> String?
) : JsFunction() {

    override fun call(receiver: Any?, args: ScriptingArgs): Any? {
        args.requireLength(1)

        val textToTransform: String? = args[0]

        return transform(textToTransform)
    }

}
