package com.testerum.web_backend.controllers.tests

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.web_backend.services.tests.TestsFrontendService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tests")
class TestsController(private val testsFrontendService: TestsFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "path") path: String): ResponseEntity<TestModel> {
        val testAtPath = testsFrontendService.getTestAtPath(
                Path.createInstance(path)
        )

        return if (testAtPath == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(testAtPath)
        }
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
}
