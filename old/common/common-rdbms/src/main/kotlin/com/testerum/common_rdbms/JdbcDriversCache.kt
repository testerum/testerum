package com.testerum.common_rdbms

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.model.resources.rdbms.connection.RdbmsDriver
import com.testerum.model.resources.rdbms.connection.RdbmsDriverWithPath
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.function.BiPredicate
import java.util.stream.Stream
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class JdbcDriversCache {

    companion object {
        private val LOG = LoggerFactory.getLogger(JdbcDriversCache::class.java)

        private val OBJECT_MAPPER = ObjectMapper()
    }

    private val lock = ReentrantReadWriteLock()

    private var configsByJarName: Map<String, RdbmsDriverWithPath> = emptyMap()

    fun initialize(jdbcDriversDirectory: JavaPath) {
        lock.write {
            val startTimeMillis = System.currentTimeMillis()

            val configsByJarName = mutableMapOf<String, RdbmsDriverWithPath>()

            Files.find(jdbcDriversDirectory, 1, filesEndingWith(".json")).use { filesStream: Stream<JavaPath> ->
                for (path: JavaPath in filesStream) {
                    val driverConfig = parseDbDriverConfig(path)
                            ?: continue

                    val jarName = driverConfig.rdbmsDriver.driverJar
                    configsByJarName[jarName] = driverConfig
                }
            }

            this.configsByJarName = configsByJarName

            val endTimeMillis = System.currentTimeMillis()

            LOG.info("loading ${configsByJarName.size} JDBC driver configs took ${endTimeMillis - startTimeMillis} ms")
        }
    }

    fun getConfigByJarName(jarName: String): RdbmsDriverWithPath? = lock.read { configsByJarName[jarName] }

    fun getDriverConfigsWithoutPath(): List<RdbmsDriver> {
        lock.read {
            return configsByJarName.values
                    .map { it.rdbmsDriver }
                    .toMutableList()
                    .sortedBy { it.name }
        }
    }

    private fun parseDbDriverConfig(dbConfigPath: JavaPath): RdbmsDriverWithPath? {
        return try {
            RdbmsDriverWithPath(
                    rdbmsDriver = OBJECT_MAPPER.readValue(
                            dbConfigPath.toFile(),
                            RdbmsDriver::class.java
                    ),
                    path = dbConfigPath.parent ?: throw IllegalArgumentException("it's not allowed to keep JDBC drivers in the root directory")
            )
        } catch(e: Exception) {
            LOG.error("driver configuration [$dbConfigPath] can't be read", e)
            null
        }
    }

    private fun filesEndingWith(end: String) = BiPredicate<JavaPath, BasicFileAttributes> { path, _ -> path.toString().endsWith(end) }
}
