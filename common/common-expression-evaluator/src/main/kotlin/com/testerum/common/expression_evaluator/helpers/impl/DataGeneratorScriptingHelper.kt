package com.testerum.common.expression_evaluator.helpers.impl

import com.github.javafaker.Faker
import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.util.JsFunction
import com.testerum.common.expression_evaluator.helpers.util.ScriptingArgs

/**
 * DataGenerator based on @see <a href="https://github.com/DiUS/java-faker">https://github.com/DiUS/java-faker</a>
 * @see <a href="https://github.com/stympy/faker">https://github.com/stympy/faker</a>
 * For API
 */
object DataGeneratorScriptingHelper : ScriptingHelper {

    private val dataGenerator = object : JsFunction(functionName = "dataGenerator") {
        override fun call(thiz: Any?, args: ScriptingArgs): Any? {
            return Faker()
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
            dataGenerator.functionName to dataGenerator
    )

}
