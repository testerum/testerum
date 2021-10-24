package database.relational.connection_manager.model

import com.testerum.common_rdbms.util.resolveConnectionUrl
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum.step_rdbms_util.scripts.DbQueryExecutor
import com.testerum.step_rdbms_util.scripts.DbScriptsExecutor
import com.testerum.step_rdbms_util.scripts.model.RdbmsResultSet
import java.sql.Connection
import java.sql.Driver
import java.util.Properties

class RdbmsConnection constructor(val rdbmsConnectionConfig: RdbmsConnectionConfig,
                                  private val driver: Driver) {

    fun openJdbcConnection() : Connection {
        val properties = Properties().apply {
            if (rdbmsConnectionConfig.user != null) {
                this["user"] = rdbmsConnectionConfig.user
            }
            if (rdbmsConnectionConfig.password != null) {
                this["password"] = rdbmsConnectionConfig.password
            }
        }

        return driver.connect(rdbmsConnectionConfig.resolveConnectionUrl(), properties)
    }

    fun executeSqlScript(sqlScript: String) {
        val dataSource = SingleConnectionDataSource(
                driver = driver,
                url = rdbmsConnectionConfig.resolveConnectionUrl(),
                username = rdbmsConnectionConfig.user,
                password = rdbmsConnectionConfig.password
        )

        val dbScriptsExecutor = DbScriptsExecutor(dataSource)

        dbScriptsExecutor.executeScript(sqlScript)
    }

    fun executeSqlStatement(sqlScript: String): RdbmsResultSet {
        val dataSource = SingleConnectionDataSource(
                driver = driver,
                url = rdbmsConnectionConfig.resolveConnectionUrl(),
                username = rdbmsConnectionConfig.user,
                password = rdbmsConnectionConfig.password
        )

        val dbStatementExecutor = DbQueryExecutor(dataSource)

        return dbStatementExecutor.executeStatement(sqlScript)
    }
}
