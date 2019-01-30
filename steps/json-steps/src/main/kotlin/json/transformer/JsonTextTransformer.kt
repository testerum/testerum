package json.transformer

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import json.model.JsonResource

class JsonTextTransformer: Transformer<JsonResource> {

    private val testVariables: TestVariables = TesterumServiceLocator.getTestVariables()

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == JsonResource::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): JsonResource {
        val sqlWithVariablesResolved: String = testVariables.resolveIn(toTransform)

        return JsonResource(sqlWithVariablesResolved)
    }

}
