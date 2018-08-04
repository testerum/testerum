package com.testerum.common.expression_evaluator.helpers

interface ScriptingHelper {

    val allowedClasses: List<Class<*>>

    val script: String

}