package database.relational.connection_manager

import com.testerum.common_rdbms.RdbmsConnectionCache
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import database.relational.connection_manager.model.RdbmsConnection

class RdbmsConnectionManager(private val rdbmsConnectionCache: RdbmsConnectionCache) {

    fun getRdbmsConnection(connectionConfig: RdbmsConnectionConfig): RdbmsConnection {
        return RdbmsConnection(
                connectionConfig,
                rdbmsConnectionCache.getDriverInstance(connectionConfig)
        )
    }

}
