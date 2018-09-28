package com.testerum.web_backend.services.tags

import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache

class TagsFrontendService(private val featuresCache: FeaturesCache,
                          private val testsCache: TestsCache,
                          private val stepsCache: StepsCache) {

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
        return testsCache.getAllTests()
                .filter { it.properties.isManual }
                .flatMap { it.tags }
    }

    private fun getFeatureTags(): List<String> = featuresCache.getAllFeatures().flatMap { it.tags }
    private fun getTestTags(): List<String> = testsCache.getAllTests().flatMap { it.tags }
    private fun getStepTags(): List<String> = stepsCache.getAllSteps().flatMap { it.tags }

}
