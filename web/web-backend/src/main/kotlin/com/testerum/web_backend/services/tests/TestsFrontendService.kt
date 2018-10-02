package com.testerum.web_backend.services.tests

import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.web_backend.services.save.SaveFrontendService

class TestsFrontendService(private val saveFrontendService: SaveFrontendService,
                           private val testsCache: TestsCache,
                           private val warningService: WarningService) {

    fun getTestAtPath(testPath: Path): TestModel? = testsCache.getTestAtPath(testPath)

    fun getWarnings(testModel: TestModel): TestModel {
        return warningService.testWithWarnings(testModel)
    }

    fun saveTest(test: TestModel): TestModel {
        return saveFrontendService.saveTest(test)
    }

    fun deleteTest(path: Path) {
        testsCache.deleteTest(path)
    }

    fun moveDirectoryOrFile(copyPath: CopyPath) {
        testsCache.moveTestDirectoryOrFile(copyPath)
    }

}
