package net.qutester.controller

import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.infrastructure.path.RenamePath
import net.qutester.model.manual.operation.UpdateTestModel
import net.qutester.model.test.TestModel
import net.qutester.service.tests.TestsService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tests")
class TestsController(private val testsService: TestsService) {
    private val LOG = LoggerFactory.getLogger(TestsController::class.java)

    @RequestMapping (params = ["path"], method = [RequestMethod.DELETE])
    fun delete(@RequestParam(value = "path") path:String) {
        testsService.remove(Path.createInstance(path));
    }

    @RequestMapping (path = ["/create"], method = [RequestMethod.POST])
    @ResponseBody
    fun create(@RequestBody testModel: TestModel): TestModel {
        return testsService.createTest(testModel);
    }

    @RequestMapping (path = ["/update"], method = [RequestMethod.POST])
    @ResponseBody
    fun update(@RequestBody updateTestModel: UpdateTestModel): TestModel {
        return testsService.updateTest(updateTestModel);
    }

    @RequestMapping (method = [RequestMethod.GET])
    @ResponseBody
    fun getTests(): List<TestModel> {
        return testsService.getAllTests();
    }


    @RequestMapping (path = ["/automated/under-path"],params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getAutomatedTestsUnderPath(@RequestParam(value = "path") path:String):List<TestModel> {
        return testsService.getTestsUnderPath(Path.createInstance(path));
    }

    @RequestMapping (params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path:String): TestModel? {
        return testsService.getTestAtPath(Path.createInstance(path));
    }

    @RequestMapping(path = ["/directory"], method = [RequestMethod.PUT])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return testsService.renameDirectory(renamePath)
    }

    @RequestMapping(path = ["/directory"], method = [RequestMethod.DELETE])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        testsService.deleteDirectory(Path.createInstance(pathAsString))
    }

    @RequestMapping(path = ["/directory/move"], method = [RequestMethod.POST])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        testsService.moveDirectoryOrFile(copyPath)
    }

}