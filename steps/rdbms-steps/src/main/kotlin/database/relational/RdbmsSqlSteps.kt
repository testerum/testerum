package database.relational

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import database.relational.connection_manager.RdbmsConnectionManager
import database.relational.connection_manager.model.RdbmsClient
import database.relational.model.RdbmsSql
import database.relational.module_bootstrapper.RdbmsStepsModuleServiceLocator
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsSqlTransformer
import org.slf4j.LoggerFactory

class RdbmsSqlSteps {

    companion object {
        private val LOG = LoggerFactory.getLogger(RdbmsSqlSteps::class.java)
    }

    private val rdbmsConnectionManager: RdbmsConnectionManager = RdbmsStepsModuleServiceLocator.bootstrapper.rdbmsStepsModuleFactory.rdbmsConnectionManager

    @When(value = "writing <<SQL>> in <<relationalDatabaseClient>> database",
          description = "This steps is executing the provided SQL in the Relational Database")
    fun writingSqlInSpecifiedDb(
            @Param(transformer= RdbmsSqlTransformer::class,
                   description = "The SQL to be executed in the database") rdbmsSql: RdbmsSql,
            @Param(transformer= RdbmsConnectionTransformer::class,
                    description = "Relational Database Client contains the information about how to connect") rdbmsClient: RdbmsClient) {

        rdbmsClient.executeSqlScript(rdbmsSql.sql)
        LOG.debug("SQL Script executed successfully")
    }

    @When(value = "writing <<SQL>> in database",
            description = "This steps is executing the provided SQL using the Relational Database Client marked as default")
    fun writingSqlInDefaultDb(
            @Param(transformer= RdbmsSqlTransformer::class,
                    description = "The SQL to be executed in the database") rdbmsSql: RdbmsSql) {
        val defaultRdbmsClient = rdbmsConnectionManager.getDefaultRdbmsClient()

        if (defaultRdbmsClient == null) {
            throw RuntimeException("No default RDBMS connections is specified")
        }

        defaultRdbmsClient.executeSqlScript(rdbmsSql.sql)
        LOG.debug("SQL Script executed successfully")
    }
}