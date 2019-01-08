package com.testerum.common.expression_evaluator.helpers.impl

import com.github.javafaker.Faker
import com.mifmif.common.regex.Generex
import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import org.intellij.lang.annotations.Language

/**
 * String generator based on a regex expression.
 * @see <a href="https://github.com/mifmif/Generex">https://github.com/mifmif/Generex</a>
 */
object GenerateStringByRegexScriptingHelper : ScriptingHelper {

    override val allowedClasses: List<Class<*>> = listOf(
            Generex::class.java
    )
    @Language("JavaScript")
    override val script: String = """
        var Generex = Java.type('${Generex::class.java.name}');

        var generateStringByRegex = function(regex) {
            return new Generex(regex).random();
        };
    """.trimIndent()
}
