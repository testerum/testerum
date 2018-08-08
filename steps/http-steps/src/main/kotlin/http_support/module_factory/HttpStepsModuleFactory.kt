package http_support.module_factory

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.common.json_diff.module_factory.CommonJsonDiffModuleFactory
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.service.module_factory.ServiceModuleFactory
import com.testerum.service.resources.http.HttpClientService
import com.testerum.step_transformer_utils.JsonVariableReplacer
import http_support.HttpMockService

@Suppress("unused", "LeakingThis")
class HttpStepsModuleFactory(context: ModuleFactoryContext,
                             commonJsonDiffModuleFactory: CommonJsonDiffModuleFactory,
                             serviceModuleFactory: ServiceModuleFactory) : BaseModuleFactory(context) {

    val httpMockService = HttpMockService(
            jsonComparer = commonJsonDiffModuleFactory.jsonComparer
    )

    // todo: break dependency with service
    val httpClientService = HttpClientService(
            httpClient = serviceModuleFactory.httpClient
    )

    val jsonVariableReplacer = JsonVariableReplacer(
            TesterumServiceLocator.getTestVariables()
    )

}