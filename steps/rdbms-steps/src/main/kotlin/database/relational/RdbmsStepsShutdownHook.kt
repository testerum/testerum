package database.relational

import com.testerum_api.testerum_steps_api.annotations.hooks.AfterAllTests
import database.relational.module_di.RdbmsStepsModuleServiceLocator

class RdbmsStepsShutdownHook {

    @AfterAllTests
    fun shutdown() {
        RdbmsStepsModuleServiceLocator.bootstrapper.context.shutdown()
    }

}
