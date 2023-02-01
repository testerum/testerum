package selenium_steps_support.service.module_di

import com.testerum.common_di.ModuleFactoryContext

class SeleniumModuleBootstrapper {

    val context = ModuleFactoryContext()

    val seleniumModuleFactory = SeleniumModuleFactory(context)

}
