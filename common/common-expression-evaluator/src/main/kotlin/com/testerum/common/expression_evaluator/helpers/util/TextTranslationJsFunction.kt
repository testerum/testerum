package com.testerum.common.expression_evaluator.helpers.util

class TextTranslationJsFunction(functionName: String,
                                private val translate: (String?) -> String?) : JsFunction(functionName) {

    override fun call(thiz: Any?, args: ScriptingArgs): Any? {
        args.requireLength(1)

        val textToTranslate: String? = args[0]

        return translate(textToTranslate)
    }

}
