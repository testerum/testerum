package json_support.module_di

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.step_transformer_utils.JsonVariableReplacer

class JsonStepsModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    val testVariables = TesterumServiceLocator.getTestVariables()

    val jsonVariableReplacer = JsonVariableReplacer(testVariables)

}
