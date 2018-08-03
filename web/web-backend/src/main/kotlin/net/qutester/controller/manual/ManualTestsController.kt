package net.qutester.controller.manual

import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.infrastructure.path.RenamePath
import net.qutester.model.manual.ManualTest
import net.qutester.model.manual.operation.UpdateManualTestModel
import net.qutester.service.manual.ManualTestsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manualTests")
class ManualTestsController(private val manualTestsService: ManualTestsService) {

    @RequestMapping (params = ["path"], method = [RequestMethod.DELETE])
    fun delete(@RequestParam(value = "path") path:String) {
        manualTestsService.remove(Path.createInstance(path))
    }

    @RequestMapping (path = ["/create"], method = [RequestMethod.POST])
    @ResponseBody
    fun create(@RequestBody manualTest: ManualTest): ManualTest {
        return manualTestsService.createTest(manualTest)
    }

    @RequestMapping (path = ["/update"], method = [RequestMethod.POST])
    @ResponseBody
    fun update(@RequestBody updateManualTestModel: UpdateManualTestModel): ManualTest {
        return manualTestsService.updateTest(updateManualTestModel)
    }

    @RequestMapping (method = [RequestMethod.GET])
    @ResponseBody
    fun getTests(): List<ManualTest> {
        return manualTestsService.getAllTests()
    }

    @RequestMapping (params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path:String): ManualTest? {
        return manualTestsService.getTestAtPath(Path.createInstance(path))
    }

    @RequestMapping(path = ["/directory"], method = [RequestMethod.PUT])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return manualTestsService.renameDirectory(renamePath)
    }

    @RequestMapping(path = ["/directory"], method = [RequestMethod.DELETE])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        manualTestsService.deleteDirectory(Path.createInstance(pathAsString))
    }

    @RequestMapping(path = ["/directory/move"], method = [RequestMethod.POST])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        manualTestsService.moveDirectoryOrFile(copyPath)
    }
}