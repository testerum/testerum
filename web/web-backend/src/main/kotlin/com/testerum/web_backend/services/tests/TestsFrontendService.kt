package com.testerum.web_backend.services.tests

import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.services.save.SaveFrontendService

class TestsFrontendService(private val webProjectManager: WebProjectManager,
                           private val saveFrontendService: SaveFrontendService,
                           private val warningService: WarningService) {

    fun getTestAtPath(testPath: Path): TestModel? = webProjectManager.getProjectServices().getTestsCache().getTestAtPath(testPath)

    fun getWarnings(testModel: TestModel): TestModel {
        return warningService.testWithWarnings(testModel)
    }

    fun saveTest(test: TestModel): TestModel {
        return saveFrontendService.saveTest(test)
    }

    fun deleteTest(path: Path) {
        webProjectManager.getProjectServices().getTestsCache().deleteTest(path)
    }
}
