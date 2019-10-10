package database.relational

import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import database.relational.connection_manager.model.RdbmsConnection
import database.relational.model.RdbmsSql
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsSqlTransformer
import database.relational.util.prettyPrint

class RdbmsSqlSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    @When(
            value = "I execute the SQL <<sql>> on the database <<dbConnection>>",
            description = "Executes the given SQL using the given relational database connection."
    )
    fun executeSql(
            @Param(
                    transformer = RdbmsSqlTransformer::class,
                    description = "The SQL to be executed."
            )
            sql: RdbmsSql,

            @Param(
                    transformer = RdbmsConnectionTransformer::class,
                    description = RdbmsSharedDescriptions.CONNECTION
            )
            dbConnection: RdbmsConnection
    ) {
        logger.info(
                "Connection config\n" +
                "-----------------\n" +
                "${dbConnection.rdbmsConnectionConfig.prettyPrint()}\n" +
                "SQL to execute\n" +
                "--------------\n" +
                "${sql.sql}\n"
        )

        dbConnection.executeSqlScript(sql.sql)
    }

}
