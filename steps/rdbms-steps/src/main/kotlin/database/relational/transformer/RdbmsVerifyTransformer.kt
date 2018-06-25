package database.relational.transformer

import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import database.relational.model.RdbmsVerify

class RdbmsVerifyTransformer(private val testVariables: TestVariables): Transformer<RdbmsVerify> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == RdbmsVerify::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): RdbmsVerify {
        val bodyWithResolved: String = testVariables.resolveIn(toTransform)

        return RdbmsVerify(bodyWithResolved)
    }

}
