package com.testerum.web_backend.controller.resources.rdbms

import com.testerum.common_rdbms.RdbmsDriverConfigService
import com.testerum.common_rdbms.RdbmsService
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum.model.resources.rdbms.connection.RdbmsDriver
import com.testerum.model.resources.rdbms.connection.RdbmsSchemasNames
import com.testerum.model.resources.rdbms.schema.RdbmsSchema
import com.testerum.service.resources.ResourcesService
import com.testerum.service.resources.rdbms.NetworkService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rdbms")
class RdbmsController(private val networkService: NetworkService,
                      private val resourcesService: ResourcesService,
                      private val rdbmsService: RdbmsService,
                      private val rdbmsDriverConfigService: RdbmsDriverConfigService) {


    @RequestMapping(method = [RequestMethod.POST], path = ["/schemas"])
    @ResponseBody
    fun schemas(@RequestBody rdbmsConnectionConfig: RdbmsConnectionConfig): RdbmsSchemasNames {

        return rdbmsService.getSchemas(rdbmsConnectionConfig)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/ping"])
    @ResponseBody
    fun ping(@RequestParam("host") host: String,
             @RequestParam("port") port: Int): Boolean {

        return networkService.respondsToPing(host, port)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/drivers"])
    @ResponseBody
    fun getRdbmsDrivers(): List<RdbmsDriver> {
        return rdbmsDriverConfigService.getDriversConfiguration()
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/schema"])
    @ResponseBody
    fun getRdbmsSchema(@RequestParam("path") rdbmsConnectionResourcePath: String): RdbmsSchema {
        val resourcePath: Path = Path.createInstance(rdbmsConnectionResourcePath)
        val connectionConfig = resourcesService.getResourceBodyAs(resourcePath, RdbmsConnectionConfig::class.java)

        return rdbmsService.getSchema(connectionConfig)
    }

}