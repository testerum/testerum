package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.date.TesterumDate
import org.intellij.lang.annotations.Language

object DateScriptingHelper : ScriptingHelper {

    override val allowedClasses: List<Class<*>> = listOf(
            TesterumDate::class.java
    )
    @Language("JavaScript")
    override val script: String = """
        var TesterumDate = Java.type('${TesterumDate::class.java.name}');

        var currentDate = function() {
            return new TesterumDate();
        };

        var date = function(year, month, dayOfMonth, hour, minute, second, nanoOfSecond) {
            if (!hour) {
                return new TesterumDate(year, month, dayOfMonth);
            } else if (!nanoOfSecond) {
                return new TesterumDate(year, month, dayOfMonth, hour, minute, second);
            } else {
                return new TesterumDate(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
            }
        };
    """.trimIndent()


}
