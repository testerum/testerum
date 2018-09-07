package com.testerum.web_backend.controller.test

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.service.save.SaveService
import com.testerum.service.tests.TestsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tests")
class TestsController(private val testsService: TestsService,
                      private val saveService: SaveService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path: String): TestModel? {
        return testsService.getTestAtPath(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        testsService.remove(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/save"])
    @ResponseBody
    fun save(@RequestBody testModel: TestModel): TestModel {
        return saveService.saveTest(testModel)
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
