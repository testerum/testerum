package database.relational

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import database.relational.connection_manager.model.RdbmsConnection
import database.relational.connection_manager.serializer.RdbmsToJsonSerializer
import database.relational.model.RdbmsVerify
import database.relational.module_di.RdbmsStepsModuleServiceLocator
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsVerifyTransformer

class RdbmsVerifySteps {

    companion object {
        private val TESTERUM_LOGGER = TesterumServiceLocator.getTesterumLogger()
    }

    private val jsonComparer: JsonComparer = RdbmsStepsModuleServiceLocator.bootstrapper.jsonDiffModuleFactory.jsonComparer

    @Then(
            value = "the state of the database <<dbConnection>> should be <<dbVerify>>",
            description = "Checks if the relational database matches an expected state."
    )
    fun verifyDbState(
            @Param(
                    transformer= RdbmsConnectionTransformer::class,
                    description = RdbmsSharedDescriptions.CONNECTION
            )
            dbConnection: RdbmsConnection,

            @Param(
                    transformer= RdbmsVerifyTransformer::class,
                    description = "The expected state of the relational database."
            )
            dbVerify: RdbmsVerify
    ) {

        val serializedSchemaAsJsonString = RdbmsToJsonSerializer.serializeSchemaAsJsonString(dbConnection)

        val compareResult: JsonCompareResult = jsonComparer.compare(dbVerify.verifyJsonAsString, serializedSchemaAsJsonString)
        if (compareResult is DifferentJsonCompareResult) {

            TESTERUM_LOGGER.debug(
                "serialized DB as JSON\n" +
                serializedSchemaAsJsonString
                        .lines()
                        .joinToString(separator = "\n") {
                            "\t" + it
                        }
            )

            throw AssertionError("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")
        }
    }

}
