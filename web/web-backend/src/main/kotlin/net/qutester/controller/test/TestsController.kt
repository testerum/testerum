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

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path: String): TestModel? {
        return testsService.getTestAtPath(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        testsService.remove(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/create"])
    @ResponseBody
    fun create(@RequestBody testModel: TestModel): TestModel {
        return testsService.createTest(testModel)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/update"])
    @ResponseBody
    fun update(@RequestBody updateTestModel: UpdateTestModel): TestModel {
        return testsService.updateTest(updateTestModel)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/automated/under-path"], params = ["path"])
    @ResponseBody
    fun getAutomatedTestsUnderPath(@RequestParam(value = "path") path: String): List<TestModel> {
        return testsService.getTestsUnderPath(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["/directory"])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        testsService.deleteDirectory(Path.createInstance(pathAsString))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/directory/move"])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        testsService.moveDirectoryOrFile(copyPath)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/warnings"])
    @ResponseBody
    fun getWarnings(@RequestBody testModel: TestModel): TestModel {
        return testsService.getWarnings(testModel, keepExistingWarnings = false)
    }

}