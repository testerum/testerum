package com.testerum.web_backend.services.steps

import com.testerum.file_service.caches.resolved.BasicStepsCache
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.web_backend.services.steps.filterer.StepsTreeFilterer

class BasicStepsFrontendService(private val basicStepsCache: BasicStepsCache) {

    fun getBasicSteps(filter: StepsTreeFilter): List<BasicStepDef> {
        return basicStepsCache.getBasicSteps()
                .filter { StepsTreeFilterer.matches(it, filter) }
    }

    fun getBasicStepAtPath(path: Path): BasicStepDef? {
        return basicStepsCache.getStepAtPath(path)
    }

}
