package net.qutester.service.resources.rdbms

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.settings.SystemSettings
import net.qutester.model.resources.rdbms.connection.RdbmsDriver
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Stream

class RdbmsDriverConfigService(val settingsManager: SettingsManager) {
    private val LOG = LoggerFactory.getLogger(RdbmsDriverConfigService::class.java)

    private var driversConfig: MutableList<RdbmsDriver> = mutableListOf<RdbmsDriver>()

    fun getDriversConfiguration(): List<RdbmsDriver> {
        if (driversConfig.isNotEmpty()) {
            return driversConfig
        }

        initializeDriversConfig()

        return driversConfig
    }

    private fun initializeDriversConfig() {
        val result = mutableListOf<RdbmsDriver>()

        val jdbcDriverDirectory = settingsManager.getSettingValue(SystemSettings.JDBC_DRIVERS_DIRECTORY)
        val driversConfigStream: Stream<Path> = Files.find(
                Paths.get(jdbcDriverDirectory),
                1,
                BiPredicate { path, _ -> path.toString().endsWith(".json") }
        )

        driversConfigStream.forEach { dbConfigPath ->
            run {
                val rdbmsDriver: RdbmsDriver? = getDbDriverConfig(dbConfigPath)
                rdbmsDriver?.let { result.add(it) }
            }
        }
        driversConfig = result.sortedBy { dbDriver -> dbDriver.name }.toMutableList()
    }

    private fun getDbDriverConfig(dbConfigPath: Path?): RdbmsDriver? {
        return try {
            val objectMapper = ObjectMapper()
            objectMapper.readValue<RdbmsDriver>(dbConfigPath?.toFile(), RdbmsDriver::class.java)
        } catch(e: Exception) {
            LOG.error("Driver configuration [${dbConfigPath.toString()}] can't be read: ", e)
            null
        }
    }
}