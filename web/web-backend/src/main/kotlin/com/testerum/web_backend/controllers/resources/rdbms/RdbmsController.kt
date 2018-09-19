package com.testerum.web_backend.controllers.resources.rdbms

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum.model.resources.rdbms.connection.RdbmsDriver
import com.testerum.model.resources.rdbms.connection.RdbmsSchemasNames
import com.testerum.model.resources.rdbms.schema.RdbmsSchema
import com.testerum.web_backend.services.resources.rdbms.RdbmsFrontendService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rdbms")
class RdbmsController(private val rdbmsFrontendService: RdbmsFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = ["/drivers"])
    @ResponseBody
    fun getRdbmsDrivers(): List<RdbmsDriver> {
        return rdbmsFrontendService.getRdbmsDrivers()
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/ping"])
    @ResponseBody
    fun canConnect(@RequestParam("host") host: String,
                   @RequestParam("port") port: Int): Boolean {
        return rdbmsFrontendService.canConnect(host, port)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/schemas"])
    @ResponseBody
    fun getSchemas(@RequestBody rdbmsConnectionConfig: RdbmsConnectionConfig): RdbmsSchemasNames {
        return rdbmsFrontendService.getSchemas(rdbmsConnectionConfig)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/schema"])
    @ResponseBody
    fun getSchema(@RequestParam("path") rdbmsConnectionResourcePathAsString: String): RdbmsSchema {
        val rdbmsConnectionResourcePath: Path = Path.createInstance(rdbmsConnectionResourcePathAsString)

        return rdbmsFrontendService.getSchema(rdbmsConnectionResourcePath)
    }

}
