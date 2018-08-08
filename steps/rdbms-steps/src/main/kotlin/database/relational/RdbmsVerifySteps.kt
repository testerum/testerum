package database.relational

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import database.relational.connection_manager.RdbmsConnectionManager
import database.relational.connection_manager.model.RdbmsClient
import database.relational.connection_manager.serializer.RdbmsToJsonSerializer
import database.relational.model.RdbmsVerify
import database.relational.module_bootstrapper.RdbmsStepsModuleServiceLocator
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsVerifyTransformer
import org.slf4j.LoggerFactory

class RdbmsVerifySteps {

    companion object {
        private val LOG = LoggerFactory.getLogger(RdbmsVerifySteps::class.java)
    }

    private val jsonComparer: JsonComparer = RdbmsStepsModuleServiceLocator.bootstrapper.commonJsonDiffModuleFactory.jsonComparer
    private val rdbmsConnectionManager: RdbmsConnectionManager = RdbmsStepsModuleServiceLocator.bootstrapper.rdbmsStepsModuleFactory.rdbmsConnectionManager

    @Then("verify database state is like in <<rdbmsVerify>> file")
    fun testDefaultDatabaseState(@Param(transformer= RdbmsVerifyTransformer::class) rdbmsVerify: RdbmsVerify) {
        val defaultRdbmsClient = rdbmsConnectionManager.getDefaultRdbmsClient()

        if (defaultRdbmsClient == null) {
            throw RuntimeException("No default RDBMS connections is specified")
        }

        val serializedSchemaAsJsonString = RdbmsToJsonSerializer.serializeSchemaAsJsonString(defaultRdbmsClient)

        val compareResult: JsonCompareResult = jsonComparer.compare(rdbmsVerify.verifyJsonAsString, serializedSchemaAsJsonString)
        if (compareResult is DifferentJsonCompareResult) {
            LOG.debug("serialized Schema As Json [${serializedSchemaAsJsonString}]")
            LOG.debug("Rdbms Verify Script executed successfully")

            LOG.error("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")

            throw AssertionError("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")
        }
    }

    @Then("verify database <<rdbmsClient>> state is like in <<rdbmsVerify>> file")
    fun testConnectionDetails(@Param(transformer= RdbmsConnectionTransformer::class) rdbmsClient: RdbmsClient,
                              @Param(transformer= RdbmsVerifyTransformer::class) rdbmsVerify: RdbmsVerify) {

        val serializedSchemaAsJsonString = RdbmsToJsonSerializer.serializeSchemaAsJsonString(rdbmsClient)

        val compareResult: JsonCompareResult = jsonComparer.compare(rdbmsVerify.verifyJsonAsString, serializedSchemaAsJsonString)
        if (compareResult is DifferentJsonCompareResult) {
            LOG.debug("serialized Schema As Json [${serializedSchemaAsJsonString}]")
            LOG.debug("Rdbms Verify Script executed successfully")

            LOG.error("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")

            throw AssertionError("=====> Assertion; message=[${compareResult.message}], path=[${compareResult.jsonPath}]")
        }
    }

}