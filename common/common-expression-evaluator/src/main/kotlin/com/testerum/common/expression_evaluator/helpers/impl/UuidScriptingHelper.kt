package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import org.intellij.lang.annotations.Language
import java.util.*

object UuidScriptingHelper : ScriptingHelper {

    override val allowedClasses: List<Class<*>> = listOf(
            UUID::class.java
    )

    @Language("JavaScript")
    override val script: String = """
        var UUID = Java.type('${UUID::class.java.name}');
        var uuid = function() {
            return UUID.randomUUID().toString();
        };
    """.trimIndent()

}
