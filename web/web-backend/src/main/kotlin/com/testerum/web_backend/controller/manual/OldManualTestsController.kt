package com.testerum.web_backend.controller.manual

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.manual.OldManualTest
import com.testerum.service.manual.ManualTestsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manualTests")
class OldManualTestsController(private val manualTestsService: ManualTestsService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getTests(): List<OldManualTest> {
        return manualTestsService.getAllTests()
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path: String): OldManualTest? {
        return manualTestsService.getTestAtPath(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        manualTestsService.remove(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/save"])
    @ResponseBody
    fun save(@RequestBody oldManualTest: OldManualTest): OldManualTest {
        return manualTestsService.save(oldManualTest)
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/directory"])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return manualTestsService.renameDirectory(renamePath)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["/directory"])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        manualTestsService.deleteDirectory(Path.createInstance(pathAsString))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/directory/move"])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        manualTestsService.moveDirectoryOrFile(copyPath)
    }

}
