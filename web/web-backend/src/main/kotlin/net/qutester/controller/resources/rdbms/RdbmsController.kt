package net.qutester.controller.resources.rdbms

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum.model.resources.rdbms.connection.RdbmsDriver
import com.testerum.model.resources.rdbms.connection.RdbmsSchemasNames
import com.testerum.model.resources.rdbms.schema.RdbmsSchema
import com.testerum.service.resources.rdbms.NetworkService
import com.testerum.service.resources.rdbms.RdbmsDriverConfigService
import com.testerum.service.resources.rdbms.RdbmsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rdbms")
class RdbmsController(private val networkService: NetworkService,
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
        return rdbmsService.getSchema(Path.createInstance(rdbmsConnectionResourcePath))
    }
}