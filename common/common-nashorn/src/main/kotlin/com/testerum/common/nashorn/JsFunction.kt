package com.testerum.common.nashorn

import org.openjdk.nashorn.api.scripting.AbstractJSObject

abstract class JsFunction : AbstractJSObject() {

    abstract val name: String
    
    abstract fun call(receiver: Any?, args: ScriptingArgs): Any?

    final override fun call(receiver: Any?, vararg args: Any?): Any? {
        val scriptingArgs = ScriptingArgs(name, args)

        return call(receiver, scriptingArgs)
    }

}
