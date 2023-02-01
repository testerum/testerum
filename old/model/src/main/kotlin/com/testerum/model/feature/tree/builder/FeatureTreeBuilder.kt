package com.testerum.model.feature.tree.builder

import com.testerum.common_kotlin.withAdditional
import com.testerum.model.feature.Feature
import com.testerum.model.feature.tree.FeatureFeatureNode
import com.testerum.model.feature.tree.FeatureNode
import com.testerum.model.feature.tree.RootFeatureNode
import com.testerum.model.feature.tree.TestFeatureNode
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class FeatureTreeBuilder(rootLabel: String) {

    private val builder = TreeBuilder(FeatureTreeBuilderCustomizer(rootLabel))

    fun addFeature(feature: Feature): Unit = builder.add(feature)

    fun addTest(test: TestModel): Unit = builder.add(test)

    fun build(): RootFeatureNode = builder.build() as RootFeatureNode

    override fun toString(): String = builder.toString()


    private class FeatureTreeBuilderCustomizer(private val rootLabel: String) : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = when (payload) {
            is Feature   -> payload.path.directories
            is TestModel -> payload.path.directories.withAdditional(getLabel(payload))
            else         -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is Feature   -> true
            is TestModel -> false
            else         -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = rootLabel

        override fun getLabel(payload: Any): String = when (payload) {
            is Feature   -> payload.path.directories.last()
            is TestModel -> payload.name + ".test"
            else         -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(payload: Any?, childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<FeatureNode> = childrenNodes as List<FeatureNode>


            var featureHasWarnings = false
            var hasHooks = false

            if (payload is Feature) {
                featureHasWarnings = payload.descendantsHaveWarnings
                hasHooks = payload.hooks.hasHooks()
            }

            val hasOwnOrDescendantWarnings: Boolean = featureHasWarnings || children.any { it.hasOwnOrDescendantWarnings }

            return RootFeatureNode(
                    name = getRootLabel(),
                    children = children,
                    hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings,
                    hasHooks = hasHooks
            )
        }

        override fun createNode(payload: Any?,
                                label: String,
                                path: List<String>,
                                childrenNodes: List<Any>,
                                indexInParent: Int): Any {
            return when (payload) {
                null, is Feature -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<FeatureNode> = childrenNodes as List<FeatureNode>

                    var featureHasWarnings = false
                    var hasHooks = false

                    if (payload is Feature) {
                        featureHasWarnings = payload.descendantsHaveWarnings
                        hasHooks = payload.hooks.hasHooks()
                    }

                    val hasOwnOrDescendantWarnings: Boolean = featureHasWarnings || children.any { it.hasOwnOrDescendantWarnings }

                    FeatureFeatureNode(
                        name = label,
                        path = Path(directories = path),
                        children = children,
                        hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings,
                        hasHooks = hasHooks
                    )
                }
                is TestModel -> TestFeatureNode(
                        name = label,
                        path = payload.path,
                        properties = payload.properties,
                        hasOwnOrDescendantWarnings = payload.hasOwnOrDescendantWarnings
                )
                else             -> throw unknownPayloadException(payload)
            }
        }
    }

}
