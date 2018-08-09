package http_support.module_di

import com.testerum.common.json_diff.module_di.JsonDiffModuleFactory
import com.testerum.common_assertion_functions.module_di.AssertionFunctionsModuleFactory
import com.testerum.common_di.ModuleFactoryContext

class HttpStepsModuleBootstrapper {

    val context = ModuleFactoryContext()

    private val assertionFunctionsModuleFactory = AssertionFunctionsModuleFactory(context)

    val jsonDiffModuleFactory = JsonDiffModuleFactory(context, assertionFunctionsModuleFactory)

    val httpStepsModuleFactory = HttpStepsModuleFactory(context, jsonDiffModuleFactory)

}