package com.testerum.common.expression_evaluator.helpers.impl

import com.github.javafaker.Faker
import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs

/**
 * DataGenerator based on @see <a href="https://github.com/DiUS/java-faker">https://github.com/DiUS/java-faker</a>
 * @see <a href="https://github.com/stympy/faker">https://github.com/stympy/faker</a>
 * For API
 */
object DataGeneratorScriptingHelper : ScriptingHelper {

    private object DataGenerator : JsFunction() {
        override val name: String
            get() = "dataGenerator"

        override fun call(receiver: Any?, args: ScriptingArgs): Any? {
            return Faker()
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
        DataGenerator.name to DataGenerator
    )

}
