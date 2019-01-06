package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.date.TesterumDate
import java.util.*

object DateScriptingHelper : ScriptingHelper {

    override val allowedClasses: List<Class<*>> = listOf(
            TesterumDate::class.java
    )

    override val script: String = """
        var TesterumDate = Java.type('${TesterumDate::class.java.name}');
        var currentDate = function() {
            return new TesterumDate();
        };
    """.trimIndent()

}