package com.testerum.common_assertion_functions.module_factory

import com.testerum.common_assertion_functions.evaluator.FunctionEvaluator
import com.testerum.common_assertion_functions.executer.DelegatingFunctionExecuter
import com.testerum.common_assertion_functions.executer.DelegatingFunctionExecuterFactory
import com.testerum.common_assertion_functions.functions.builtin.RegexFunctions
import com.testerum.common_assertion_functions.functions.builtin.TextFunctions
import com.testerum.common_assertion_functions.functions.builtin.TypeCastFunctions
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext

@Suppress("unused", "LeakingThis")
class CommonAssertionFunctionsModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    // to be overridden for customization
    val customFunctions: List<Any> = emptyList()

    private val builtInFunctions: List<Any> = listOf(
            RegexFunctions,
            TextFunctions,
            TypeCastFunctions
    )

    val allFunctions: List<Any> = customFunctions + builtInFunctions

    val delegatingFunctionExecuter: DelegatingFunctionExecuter = DelegatingFunctionExecuterFactory.createDelegatingFunctionExecuter(allFunctions)

    val functionEvaluator: FunctionEvaluator = FunctionEvaluator(delegatingFunctionExecuter)

}