package http_support.module_di

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.common.json_diff.module_di.JsonDiffModuleFactory
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_httpclient.HttpClientService
import com.testerum.step_transformer_utils.JsonVariableReplacer
import http_support.HttpMockService
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients

class HttpStepsModuleFactory(context: ModuleFactoryContext,
                             jsonDiffModuleFactory: JsonDiffModuleFactory) : BaseModuleFactory(context) {

    val httpMockService = HttpMockService(
            jsonComparer = jsonDiffModuleFactory.jsonComparer
    )

    private val httpClient: HttpClient = HttpClients.createDefault().also {
        context.registerShutdownHook {
            it.close()
        }
    }

    val httpClientService = HttpClientService(httpClient)


    val jsonVariableReplacer = JsonVariableReplacer(
            TesterumServiceLocator.getTestVariables()
    )

}