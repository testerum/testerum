package database.relational.connection_manager

import com.testerum.common_rdbms.RdbmsConnectionCache
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import database.relational.connection_manager.model.RdbmsClient

class RdbmsConnectionManager(private val rdbmsConnectionCache: RdbmsConnectionCache) {

    fun getRdbmsClient(connectionConfig: RdbmsConnectionConfig): RdbmsClient {
        return RdbmsClient(
                connectionConfig,
                rdbmsConnectionCache.getDriverInstance(connectionConfig)
        )
    }

}
