package com.testerum.common_assertion_functions.module_di

import com.testerum.common_assertion_functions.evaluator.FunctionEvaluator
import com.testerum.common_assertion_functions.executer.DelegatingFunctionExecuter
import com.testerum.common_assertion_functions.executer.DelegatingFunctionExecuterFactory
import com.testerum.common_assertion_functions.functions.builtin.RegexFunctions
import com.testerum.common_assertion_functions.functions.builtin.TextFunctions
import com.testerum.common_assertion_functions.functions.builtin.TypeCastFunctions
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext

class AssertionFunctionsModuleFactory(context: ModuleFactoryContext,
                                      customFunctionsCreator: () -> List<Any> = { emptyList() }) : BaseModuleFactory(context) {

    private val builtInFunctions: List<Any> = listOf(
            RegexFunctions,
            TextFunctions,
            TypeCastFunctions
    )

    private val allFunctions: List<Any> = customFunctionsCreator() + builtInFunctions

    private val delegatingFunctionExecuter: DelegatingFunctionExecuter = DelegatingFunctionExecuterFactory.createDelegatingFunctionExecuter(allFunctions)

    val functionEvaluator: FunctionEvaluator = FunctionEvaluator(delegatingFunctionExecuter)

}