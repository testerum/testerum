package com.testerum.common.expression_evaluator.helpers.impl

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.date.TesterumDate
import com.testerum.common.nashorn.JsFunction
import com.testerum.common.nashorn.ScriptingArgs

object DateScriptingHelper : ScriptingHelper {

    private object CurrentDateJsFunction: JsFunction() {
        override val name: String
            get() = "currentDate"

        override fun call(receiver: Any?, args: ScriptingArgs): Any? {
            return TesterumDate()
        }
    }

    private object DateJsFunction: JsFunction() {
        override val name: String
            get() = "date"

        override fun call(receiver: Any?, args: ScriptingArgs): Any? {
            when (args.size) {
                3 -> {
                    val year: Int = args[0]
                    val month: Int = args[1]
                    val dayOfMonth: Int = args[2]

                    return TesterumDate(year, month, dayOfMonth)
                }
                6 -> {
                    val year: Int = args[0]
                    val month: Int = args[1]
                    val dayOfMonth: Int = args[2]
                    val hour: Int = args[3]
                    val minute: Int = args[4]
                    val second: Int = args[5]

                    return TesterumDate(year, month, dayOfMonth, hour, minute, second)
                }
                7 -> {
                    val year: Int = args[0]
                    val month: Int = args[1]
                    val dayOfMonth: Int = args[2]
                    val hour: Int = args[3]
                    val minute: Int = args[4]
                    val second: Int = args[5]
                    val nanoOfSecond: Int = args[6]

                    return TesterumDate(year, month, dayOfMonth, hour, minute, second, nanoOfSecond)
                }
                else -> throw IllegalArgumentException("function [date] accepts 3, 6, or 7 arguments, but got ${args.size}")
            }
        }
    }

    override val globalVariables: Map<String, Any?> = mapOf(
        CurrentDateJsFunction.name to CurrentDateJsFunction,
        DateJsFunction.name to DateJsFunction
    )

}
