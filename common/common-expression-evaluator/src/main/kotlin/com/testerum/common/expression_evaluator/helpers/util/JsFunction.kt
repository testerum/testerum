package com.testerum.common.expression_evaluator.helpers.util

import jdk.nashorn.api.scripting.AbstractJSObject

abstract class JsFunction(val functionName: String) : AbstractJSObject() {

    override fun call(thiz: Any?, vararg args: Any?): Any? {
        val scriptingArgs = ScriptingArgs(functionName, args)

        return call(thiz, scriptingArgs)
    }

    abstract fun call(thiz: Any?, args: ScriptingArgs): Any?

}
