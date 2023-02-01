package http_support.module_di

import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum.common.json_diff.module_di.JsonDiffModuleFactory
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_httpclient.TesterumHttpClientFactory
import com.testerum.step_transformer_utils.JsonVariableReplacer
import http_support.HttpMockService
import http_support.HttpStepsSettingsManager
import org.apache.http.client.HttpClient

class HttpStepsModuleFactory(context: ModuleFactoryContext,
                             jsonDiffModuleFactory: JsonDiffModuleFactory) : BaseModuleFactory(context) {

    val httpMockService = HttpMockService(
            jsonComparer = jsonDiffModuleFactory.jsonComparer
    )

    private val httpClient: HttpClient = TesterumHttpClientFactory.createHttpClient().also {
        context.registerShutdownHook {
            it.close()
        }
    }

    val httpClientService = HttpClientService(httpClient)

    val httpStepsSettingsManager = HttpStepsSettingsManager(
            runnerSettingsManager = TesterumServiceLocator.getSettingsManager()
    )

    val jsonVariableReplacer = JsonVariableReplacer(
            TesterumServiceLocator.getTestVariables()
    )

}
