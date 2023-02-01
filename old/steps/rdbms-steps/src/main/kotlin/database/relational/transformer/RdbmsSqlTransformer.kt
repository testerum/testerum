package database.relational.transformer

import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer
import database.relational.model.RdbmsSql

class RdbmsSqlTransformer: Transformer<RdbmsSql> {

    private val testVariables: TestVariables = TesterumServiceLocator.getTestVariables()

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == RdbmsSql::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): RdbmsSql {
        val sqlWithVariablesResolved: String = testVariables.resolveIn(toTransform)

        return RdbmsSql(sqlWithVariablesResolved)
    }

}
