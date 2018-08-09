package com.testerum.common_rdbms

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.model.resources.rdbms.connection.RdbmsDriver
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import java.util.stream.Stream
import kotlin.streams.asSequence

class RdbmsDriverConfigService(private val jdbcDriversDirectory: Path) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RdbmsDriverConfigService::class.java)

        private val OBJECT_MAPPER = ObjectMapper()
    }

    private val driversConfigs: List<RdbmsDriver> by lazy { loadDriversConfigs() }

    fun getDriversConfiguration(): List<RdbmsDriver> = driversConfigs

    private fun loadDriversConfigs(): List<RdbmsDriver> {
        Files.find(jdbcDriversDirectory, 1, filesEndingWith(".json")).use { filesStream: Stream<Path> ->
            return filesStream.asSequence()
                    .map { parseDbDriverConfig(it) }
                    .filterNotNull()
                    .sortedBy { it.name }
                    .toList()
        }
    }

    private fun parseDbDriverConfig(dbConfigPath: Path): RdbmsDriver? {
        return try {
            OBJECT_MAPPER.readValue<RdbmsDriver>(
                    dbConfigPath.toFile(),
                    RdbmsDriver::class.java
            )
        } catch(e: Exception) {
            LOG.error("Driver configuration [$dbConfigPath] can't be read: ", e)
            null
        }
    }

    private fun filesEndingWith(end: String) = BiPredicate<Path, BasicFileAttributes> { path, _ -> path.toString().endsWith(end) }
}