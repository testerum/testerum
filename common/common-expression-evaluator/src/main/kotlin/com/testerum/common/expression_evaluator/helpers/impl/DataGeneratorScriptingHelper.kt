package com.testerum.common.expression_evaluator.helpers.impl

import com.github.javafaker.Faker
import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import org.intellij.lang.annotations.Language

/**
 * DataGenerator based on @see <a href="https://github.com/DiUS/java-faker">https://github.com/DiUS/java-faker</a>
 * @see <a href="https://github.com/stympy/faker">https://github.com/stympy/faker</a>
 * For API
 */
object DataGeneratorScriptingHelper : ScriptingHelper {

    override val allowedClasses: List<Class<*>> = listOf(
            Faker::class.java
    )
    @Language("JavaScript")
    override val script: String = """
        var Faker = Java.type('${Faker::class.java.name}');

        var dataGenerator = function() {
            return new Faker();
        };
    """.trimIndent()
}
