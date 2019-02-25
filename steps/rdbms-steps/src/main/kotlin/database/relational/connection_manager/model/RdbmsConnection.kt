package database.relational.connection_manager.model

import com.testerum.common_rdbms.util.resolveConnectionUrl
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import org.garnishtest.modules.generic.db_util.scripts.DbScriptsExecutor
import org.garnishtest.modules.generic.variables_resolver.impl.MapBasedVariablesResolver
import java.sql.Connection
import java.sql.Driver
import java.util.*

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
        val mapBasedVariablesResolver = MapBasedVariablesResolver("\${", "}")

        dbScriptsExecutor.executeScript(mapBasedVariablesResolver, sqlScript)
    }

}
