package com.testerum.web_backend.services.tags

import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.web_backend.services.project.WebProjectManager

class TagsFrontendService(private val webProjectManager: WebProjectManager) {

    fun getAllTags(): List<String> {
        val result = mutableListOf<String>().apply {
            addAll(getFeatureTags())
            addAll(getTestTags())
            addAll(getStepTags())
        }

        // sort and keep only distinct values at the sa

        return result.distinctBy(String::toLowerCase)
                .sortedBy(String::toLowerCase)
    }

    fun getManualTestsTags(): List<String> {
        return webProjectManager.getProjectServices().getTestsCache().getAllTests()
                .filter { it.properties.isManual }
                .flatMap { it.tags }
    }

    private fun getFeatureTags(): List<String> = webProjectManager.getProjectServices().getFeatureCache().getAllFeatures().flatMap { it.tags }
    private fun getTestTags(): List<String> = webProjectManager.getProjectServices().getTestsCache().getAllTests().flatMap { it.tags }
    private fun getStepTags(): List<String> = webProjectManager.getProjectServices().getStepsCache().getAllSteps().flatMap {
        when (it) {
            is BasicStepDef    -> it.tags
            is ComposedStepDef -> it.tags
            else               -> emptyList()
        }
    }

}
