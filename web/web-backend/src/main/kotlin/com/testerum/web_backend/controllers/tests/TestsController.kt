package com.testerum.web_backend.controllers.tests

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.web_backend.services.tests.TestsFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tests")
class TestsController(private val testsFrontendService: TestsFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path: String): TestModel? {
        return testsFrontendService.getTestAtPath(
                Path.createInstance(path)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/warnings"])
    @ResponseBody
    fun getWarnings(@RequestBody test: TestModel): TestModel {
        return testsFrontendService.getWarnings(test)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/save"])
    @ResponseBody
    fun save(@RequestBody test: TestModel): TestModel {
        return testsFrontendService.saveTest(test)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        testsFrontendService.deleteTest(
                Path.createInstance(path)
        )
    }

    // todo: refactor:
    // - in the UI, if the node we drag is a feature, call "POST /features?path=:path" to move the feature
    // - in the UI, if the node we drag is a test, call "POST /tests/save" to move the test
    @RequestMapping(method = [RequestMethod.POST], path = ["/directory/move"])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        testsFrontendService.moveDirectoryOrFile(copyPath)
    }

}
