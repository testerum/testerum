package com.testerum.web_backend.services.steps

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.web_backend.services.steps.filterer.StepsTreeFilterer

class BasicStepsFrontendService(private val stepsCache: StepsCache) {

    fun getBasicSteps(filter: StepsTreeFilter): List<BasicStepDef> {
        return stepsCache.getBasicSteps()
                .filter { StepsTreeFilterer.matches(it, filter) }
    }

    fun getBasicStepAtPath(path: Path): BasicStepDef? {
        val step = stepsCache.getStepAtPath(path)
                ?: return null

        if (step !is BasicStepDef) {
            return null
        }

        return step
    }

}
