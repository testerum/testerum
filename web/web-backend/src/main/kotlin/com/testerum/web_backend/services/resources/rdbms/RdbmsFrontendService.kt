package com.testerum.web_backend.services.resources.rdbms

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.common_rdbms.JdbcDriversCache
import com.testerum.common_rdbms.RdbmsConnectionCache
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum.model.resources.rdbms.connection.RdbmsDriver
import com.testerum.model.resources.rdbms.connection.RdbmsSchemasNames
import com.testerum.model.resources.rdbms.schema.RdbmsSchema
import com.testerum.web_backend.services.resources.NetworkService
import com.testerum.web_backend.services.resources.ResourcesFrontendService

class RdbmsFrontendService(private val networkService: NetworkService,
                           private val resourcesService: ResourcesFrontendService, // todo: we shouldn't be using frontend services from other services (depend on another layer that does body transformation (JSON <--> YAML))
                           private val rdbmsConnectionCache: RdbmsConnectionCache,
                           private val jdbcDriversCache: JdbcDriversCache) {

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun getRdbmsDrivers(): List<RdbmsDriver> {
        return jdbcDriversCache.getDriverConfigsWithoutPath()
    }

    fun canConnect(host: String, port: Int): Boolean {
        return networkService.canConnect(host, port)
    }

    fun getSchemas(rdbmsConnectionConfig: RdbmsConnectionConfig): RdbmsSchemasNames {
        return rdbmsConnectionCache.getSchemas(rdbmsConnectionConfig)
    }

    fun getSchema(path: Path): RdbmsSchema {
        val resource = resourcesService.getResourceAtPath(path)
                ?: throw RuntimeException ("No resource on the path ["+path.toString()+"] was found")
        val connectionConfig: RdbmsConnectionConfig = OBJECT_MAPPER.readValue(resource.body, RdbmsConnectionConfig::class.java)

        return rdbmsConnectionCache.getSchema(connectionConfig)
    }

}
