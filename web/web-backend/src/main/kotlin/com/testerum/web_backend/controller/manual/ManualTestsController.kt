package com.testerum.web_backend.controller.manual

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.operation.UpdateManualTestModel
import com.testerum.service.manual.ManualTestsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manualTests")
class ManualTestsController(private val manualTestsService: ManualTestsService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getTests(): List<ManualTest> {
        return manualTestsService.getAllTests()
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path: String): ManualTest? {
        return manualTestsService.getTestAtPath(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        manualTestsService.remove(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/create"])
    @ResponseBody
    fun create(@RequestBody manualTest: ManualTest): ManualTest {
        return manualTestsService.createTest(manualTest)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/update"])
    @ResponseBody
    fun update(@RequestBody updateManualTestModel: UpdateManualTestModel): ManualTest {
        return manualTestsService.updateTest(updateManualTestModel)
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