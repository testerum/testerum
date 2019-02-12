package database.relational

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import database.relational.connection_manager.model.RdbmsClient
import database.relational.model.RdbmsSql
import database.relational.transformer.RdbmsConnectionTransformer
import database.relational.transformer.RdbmsSqlTransformer

class RdbmsSqlSteps {

    private val testerumLogger = TesterumServiceLocator.getTesterumLogger()

    @When(
            value = "writing <<sql>> in <<dbConnection>> database",
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
            dbConnection: RdbmsClient
    ) {
        testerumLogger.debug(
                "SQL to execute\n"
                + sql.sql.lines()
                         .joinToString(separator = "\n") {
                             "\t" + it
                         }

        )
        dbConnection.executeSqlScript(sql.sql)
    }

}
