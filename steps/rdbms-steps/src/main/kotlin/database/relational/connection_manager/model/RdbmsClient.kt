package database.relational.connection_manager.model

import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum.service.resources.rdbms.util.resolveConnectionUrl
import org.garnishtest.modules.generic.db_util.scripts.DbScriptsExecutor
import org.garnishtest.modules.generic.variables_resolver.impl.MapBasedVariablesResolver
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import java.sql.Connection
import java.sql.Driver
import java.util.*

class RdbmsClient constructor(val rdbmsConnectionConfig: RdbmsConnectionConfig,
                              private val driver: Driver){

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
        val properties = Properties().apply {
            if (rdbmsConnectionConfig.user != null) {
                this["user"] = rdbmsConnectionConfig.user
            }
            if (rdbmsConnectionConfig.password != null) {
                this["password"] = rdbmsConnectionConfig.password
            }
        }

        val dataSource = SimpleDriverDataSource(driver, rdbmsConnectionConfig.resolveConnectionUrl(), properties)

        val dbScriptsExecutor = DbScriptsExecutor(dataSource)
        val mapBasedVariablesResolver = MapBasedVariablesResolver("\${", "}")
        //TODO: add context variable to resolver

        dbScriptsExecutor.executeScripts(mapBasedVariablesResolver, sqlScript)
    }
}