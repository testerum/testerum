package database.relational.connection_manager

import com.testerum.common_rdbms.RdbmsService
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import database.relational.connection_manager.model.RdbmsClient

class RdbmsConnectionManager(private val rdbmsService: RdbmsService) {

    fun getRdbmsClient(connectionConfig: RdbmsConnectionConfig): RdbmsClient {
        return RdbmsClient(
                connectionConfig,
                rdbmsService.getDriverInstance(connectionConfig)
        )
    }

}
