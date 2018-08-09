package database.relational

import com.testerum.api.annotations.hooks.AfterAllTests
import database.relational.module_di.RdbmsStepsModuleServiceLocator

class RdbmsStepsShutdownHook {

    @AfterAllTests
    fun shutdown() {
        RdbmsStepsModuleServiceLocator.bootstrapper.context.shutdown()
    }

}