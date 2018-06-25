package net.qutester.controller.resources.rdbms

import net.qutester.model.infrastructure.path.Path
import net.qutester.model.resources.rdbms.connection.RdbmsConnectionConfig
import net.qutester.model.resources.rdbms.connection.RdbmsDriver
import net.qutester.model.resources.rdbms.connection.RdbmsSchemasNames
import net.qutester.model.resources.rdbms.schema.RdbmsSchema
import net.qutester.service.resources.rdbms.NetworkService
import net.qutester.service.resources.rdbms.RdbmsDriverConfigService
import net.qutester.service.resources.rdbms.RdbmsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rdbms")
class RdbmsController(private val networkService: NetworkService,
                      private val rdbmsService: RdbmsService,
                      private val rdbmsDriverConfigService: RdbmsDriverConfigService) {


    @RequestMapping(path = arrayOf("/schemas"), method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun schemas(@RequestBody rdbmsConnectionConfig: RdbmsConnectionConfig): RdbmsSchemasNames {

        return rdbmsService.getSchemas(rdbmsConnectionConfig)
    }

    @RequestMapping(path = arrayOf("/ping"), method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun ping(@RequestParam("host") host: String,
             @RequestParam("port") port: Int): Boolean {

        return networkService.respondsToPing(host, port);
    }

    @RequestMapping(path = arrayOf("/drivers"), method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getRdbmsDrivers(): List<RdbmsDriver> {
        return rdbmsDriverConfigService.getDriversConfiguration();
    }

    @RequestMapping(path = arrayOf("/schema"), method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getRdbmsSchema(@RequestParam("path") rdbmsConnectionResourcePath: String): RdbmsSchema {
        return rdbmsService.getSchema(Path.createInstance(rdbmsConnectionResourcePath));
    }
}