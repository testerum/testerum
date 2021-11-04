package database.relational

import com.testerum.model.expressions.json.util.JSON_STEPS_OBJECT_MAPPER
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import database.relational.connection_manager.model.RdbmsConnection
import database.relational.model.RdbmsSql
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsSqlTransformer
import database.relational.util.prettyPrint

class RdbmsSqlSteps {
    companion object {
        private val PRETTY_PRINTING_JSON_WRITER = JSON_STEPS_OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
    }

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()

    @When(
            value = "I execute the SQL <<sqlScript>> on the database <<dbConnection>>",
            description = "Executes the given SQL using the given relational database connection."
    )
    fun executeSql(
        @Param(
                    transformer = RdbmsSqlTransformer::class,
                    description = "The SQL to be executed."
            )
            sqlScript: RdbmsSql,

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
                "${sqlScript.sql}\n"
        )

        dbConnection.executeSqlScript(sqlScript.sql)
    }

    @When(
            value = "I execute the SQL query <<sqlQuery>> on the database <<dbConnection>> and save the result into the variable <<varName>>",
            description = "Executes the given SQL query, and saves the result into the variable with the given name. " +
                "The variable will be an array, each item representing a row returned by the SQL query."
    )
    fun executeSqlAndSaveTheResultSet(
        @Param(
                    transformer = RdbmsSqlTransformer::class,
                    description = "The SQL to be executed."
            )
            sqlQuery: RdbmsSql,

        @Param(
                    transformer = RdbmsConnectionTransformer::class,
                    description = RdbmsSharedDescriptions.CONNECTION
            )
            dbConnection: RdbmsConnection,

        @Param(
                    description = ""
            )
            resultName: String
    ) {
        logger.info(
                "Connection config\n" +
                "-----------------\n" +
                "${dbConnection.rdbmsConnectionConfig.prettyPrint()}\n" +
                "SQL to execute\n" +
                "--------------\n" +
                "${sqlQuery.sql}\n"
        )

        val rdbmsResultSet = dbConnection.executeSqlStatement(sqlQuery.sql)
        variables[resultName] = rdbmsResultSet.result

        val rdbmsResultSetAsString = PRETTY_PRINTING_JSON_WRITER.writeValueAsString(variables[resultName])
        logger.info(
            "Creating variable \"$resultName\":\n" +
                "$rdbmsResultSetAsString\n"
        )

    }
}
