package database.relational

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsSqlTransformer
import database.relational.connection_manager.RdbmsConnectionManager
import database.relational.connection_manager.model.RdbmsClient
import database.relational.model.RdbmsSql
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class RdbmsSqlSteps(@Autowired val rdbmsConnectionManager: RdbmsConnectionManager) {
    private val LOG = LoggerFactory.getLogger(RdbmsSqlSteps::class.java)


    @When(value = "writing  <<SQL>> in <<relationalDatabaseClient>> database",
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