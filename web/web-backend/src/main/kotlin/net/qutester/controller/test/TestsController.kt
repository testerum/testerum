package net.qutester.controller.test

import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.manual.operation.UpdateTestModel
import net.qutester.model.test.TestModel
import net.qutester.service.tests.TestsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tests")
class TestsController(private val testsService: TestsService) {

    @RequestMapping(params = ["path"], method = [RequestMethod.DELETE])
    fun delete(@RequestParam(value = "path") path: String) {
        testsService.remove(Path.createInstance(path))
    }

    @RequestMapping(path = ["/create"], method = [RequestMethod.POST])
    @ResponseBody
    fun create(@RequestBody testModel: TestModel): TestModel {
        return testsService.createTest(testModel)
    }

    @RequestMapping(path = ["/update"], method = [RequestMethod.POST])
    @ResponseBody
    fun update(@RequestBody updateTestModel: UpdateTestModel): TestModel {
        return testsService.updateTest(updateTestModel);
    }

    @RequestMapping(path = ["/automated/under-path"], params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getAutomatedTestsUnderPath(@RequestParam(value = "path") path: String): List<TestModel> {
        return testsService.getTestsUnderPath(Path.createInstance(path));
    }

    @RequestMapping(params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path: String): TestModel? {
        return testsService.getTestAtPath(Path.createInstance(path));
    }

    @RequestMapping(path = ["/directory"], method = [RequestMethod.DELETE])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        testsService.deleteDirectory(Path.createInstance(pathAsString))
    }

    @RequestMapping(path = ["/directory/move"], method = [RequestMethod.POST])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        testsService.moveDirectoryOrFile(copyPath)
    }

    @RequestMapping(path = ["/warnings"], method = [RequestMethod.POST])
    @ResponseBody
    fun getWarnings(@RequestBody testModel: TestModel): TestModel {
        return testsService.getWarnings(testModel, keepExistingWarnings = false)
    }

}