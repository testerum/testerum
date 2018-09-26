package com.testerum.web_backend.services.features.filterer

import com.testerum.common_jdk.containsSearchStringParts
import com.testerum.model.feature.Feature
import com.testerum.model.feature.filter.FeaturesTreeFilter
import com.testerum.model.feature.tree.FeatureFeatureNode
import com.testerum.model.feature.tree.FeatureNode
import com.testerum.model.feature.tree.RootFeatureNode
import com.testerum.model.feature.tree.TestFeatureNode
import com.testerum.model.test.TestModel
import org.slf4j.LoggerFactory

object FeaturesTreeFilterer {

    private val LOG = LoggerFactory.getLogger(FeaturesTreeFilterer::class.java)

    fun filterFeatures(features: Collection<Feature>, filter: FeaturesTreeFilter): List<Feature> {
        val results = mutableListOf<Feature>()

        for (feature in features) {
            val matchesTextSearch = featureMatchesTextSearch(feature, filter)
            val matchesTags = matchesTags(feature.tags, filter)

            if (matchesTextSearch && matchesTags) {
                results.add(feature)
            }
        }

        return results
    }

    private fun featureMatchesTextSearch(feature: Feature, filter: FeaturesTreeFilter): Boolean {
        if (feature.path.toString().containsSearchStringParts(filter.search)) {
            return true
        }

        if (feature.name.containsSearchStringParts(filter.search)) {
            return true
        }

        if (feature.description.containsSearchStringParts(filter.search)) {
            return true
        }

        return false
    }

    private fun matchesTags(tags: List<String>, filer: FeaturesTreeFilter): Boolean {
        val featureUpperCasedTags = tags.map(String::toUpperCase)

        return featureUpperCasedTags.containsAll(
                filer.tags.map(String::toUpperCase)
        )
    }

    fun filterTests(tests: Collection<TestModel>, filter: FeaturesTreeFilter): List<TestModel> {
        val results = mutableListOf<TestModel>()
        for (test in tests) {
            val testMatchesTypeFilter = testMatchesType(test, filter)
            val testMatchesTestFilter = testMatchesTextSearch(test, filter)
            val matchesTags = matchesTags(test.tags, filter)

            if (testMatchesTypeFilter && testMatchesTestFilter && matchesTags) {
                results.add(test)
            }
        }

        return results
    }

    private fun testMatchesType(test: TestModel, filter: FeaturesTreeFilter): Boolean {
        return test.properties.isManual == filter.includeManualTests ||
                !test.properties.isManual == filter.includeAutomatedTests
    }

    private fun testMatchesTextSearch(test: TestModel, filter: FeaturesTreeFilter): Boolean {
        if (test.path.toString().containsSearchStringParts(filter.search)) {
            return true
        }

        if (test.text.containsSearchStringParts(filter.search)) {
            return true
        }

        return false
    }

    fun filterOutEmptyFeaturesIfNeeded(rootFeatureNode: RootFeatureNode, filter: FeaturesTreeFilter): RootFeatureNode {
        if (filter.includeEmptyFeatures) {
            return rootFeatureNode
        }

        return filterOutEmptyFeaturesForRoot(rootFeatureNode)
    }

    private fun filterOutEmptyFeatures(featureNode: FeatureNode): FeatureNode? {
        return when (featureNode) {
            is RootFeatureNode -> filterOutEmptyFeaturesForRoot(featureNode)
            is FeatureFeatureNode -> filterOutEmptyFeaturesForFeature(featureNode)
            is TestFeatureNode -> filterOutEmptyFeaturesForTest(featureNode)
            else -> {
                LOG.error("unknown node of type [${featureNode.javaClass.name}] will not be filtered")
                featureNode
            }
        }
    }

    private fun filterOutEmptyFeaturesForRoot(rootNode: RootFeatureNode): RootFeatureNode {
        val filteredChildren = rootNode.children
                .map { filterOutEmptyFeatures(it) }
                .filterNotNull()

        return rootNode.copy(
                children = filteredChildren
        )
    }

    private fun filterOutEmptyFeaturesForFeature(featureNode: FeatureFeatureNode): FeatureFeatureNode? {
        val filteredChildren = featureNode.children
                .map { filterOutEmptyFeatures(it) }
                .filterNotNull()

        return if (filteredChildren.isEmpty()) {
            null
        } else {
            featureNode.copy(
                    children = filteredChildren
            )
        }
    }

    private fun filterOutEmptyFeaturesForTest(featureNode: TestFeatureNode): TestFeatureNode {
        return featureNode
    }

}
