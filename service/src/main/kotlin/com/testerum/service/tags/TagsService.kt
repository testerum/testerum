package com.testerum.service.tags

import com.testerum.service.feature.FeatureService
import com.testerum.service.step.StepCache
import com.testerum.service.tests.TestsService

class TagsService(private val featureService: FeatureService,
                  private val testsService: TestsService,
                  private val stepCache: StepCache) {

    fun getAllTags(): List<String> {
        // todo: cache the tags, since reading all files is slow when we have a lot of them

        val result = mutableListOf<String>().apply {
            addAll(getFeatureTags())
            addAll(getTestTags())
            addAll(getStepTags())
        }

        return result.distinctBy(String::toLowerCase)
                     .sortedBy(String::toLowerCase)
    }

    private fun getFeatureTags(): List<String> = featureService.getAllFeatures().flatMap { it.tags }
    private fun getTestTags(): List<String>    = testsService.getAllTests().flatMap { it.tags }
    private fun getStepTags(): List<String>    = stepCache.getAllSteps().flatMap { it.tags }

}
