package com.testerum.service_it.service_it_tests.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.service.module_di.ServiceModuleFactory
import com.testerum.service_it.service_it_tests.steps.ServiceItTestsSteps

class ServiceItTestsModuleFactory(context: ModuleFactoryContext,
                                  serviceModuleFactory: ServiceModuleFactory) : BaseModuleFactory(context) {

    val steps = ServiceItTestsSteps()

}
