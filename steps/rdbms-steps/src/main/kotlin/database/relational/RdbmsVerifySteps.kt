package database.relational

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import database.relational.connection_manager.model.RdbmsClient
import database.relational.connection_manager.serializer.RdbmsToJsonSerializer
import database.relational.model.RdbmsVerify
import database.relational.module_di.RdbmsStepsModuleServiceLocator
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsVerifyTransformer
import org.slf4j.LoggerFactory

class RdbmsVerifySteps {

    companion object {
        private val LOG = LoggerFactory.getLogger(RdbmsVerifySteps::class.java)
    }

    private val jsonComparer: JsonComparer = RdbmsStepsModuleServiceLocator.bootstrapper.jsonDiffModuleFactory.jsonComparer

    @Then(
            value = "verify database <<rdbmsClient>> state is like in <<rdbmsVerify>> file",
            description = "Checks if the relational database matches an expected state."
    )
    fun testConnectionDetails(
            @Param(
                    transformer= RdbmsConnectionTransformer::class,
                    description = RdbmsSharedDescriptions.CONNECTION
            )
            rdbmsClient: RdbmsClient,

            @Param(
                    transformer= RdbmsVerifyTransformer::class,
                    description = "The expected state of the relational database."
            )
            rdbmsVerify: RdbmsVerify
    ) {

        val serializedSchemaAsJsonString = RdbmsToJsonSerializer.serializeSchemaAsJsonString(rdbmsClient)

        val compareResult: JsonCompareResult = jsonComparer.compare(rdbmsVerify.verifyJsonAsString, serializedSchemaAsJsonString)
        if (compareResult is DifferentJsonCompareResult) {
            LOG.debug("serialized Schema As Json [$serializedSchemaAsJsonString]")
            LOG.debug("Rdbms Verify Script executed successfully")

            LOG.error("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")

            throw AssertionError("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")
        }
    }

}
