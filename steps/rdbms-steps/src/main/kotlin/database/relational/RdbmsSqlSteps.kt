package database.relational

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import database.relational.connection_manager.model.RdbmsClient
import database.relational.model.RdbmsSql
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsSqlTransformer
import org.slf4j.LoggerFactory

class RdbmsSqlSteps {

    companion object {
        private val LOG = LoggerFactory.getLogger(RdbmsSqlSteps::class.java)
    }

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

}
