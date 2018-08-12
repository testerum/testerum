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

class FeatureTreeBuilder {

    private val builder = TreeBuilder(FeatureTreeBuilderCustomizer)

    fun addFeature(feature: Feature): Unit = builder.add(feature)

    fun addTest(test: TestModel): Unit = builder.add(test)

    fun build(): RootFeatureNode = builder.build() as RootFeatureNode

    override fun toString(): String = builder.toString()


    private object FeatureTreeBuilderCustomizer : TreeBuilderCustomizer {
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

        override fun getRootLabel(): String = "Features"

        override fun getLabel(payload: Any): String = when (payload) {
            is Feature   -> payload.path.directories.last()
            is TestModel -> payload.text
            else         -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<FeatureNode> = childrenNodes as List<FeatureNode>

            val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

            return RootFeatureNode(
                    name = getRootLabel(),
                    children = children,
                    hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings
            )
        }

        override fun createNode(payload: Any?,
                                label: String,
                                path: List<String>,
                                childrenNodes: List<Any>,
                                indexInParent: Int): Any {
            @Suppress("UNCHECKED_CAST")
            return when (payload) {
                null, is Feature -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<FeatureNode> = childrenNodes as List<FeatureNode>

                    val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

                    FeatureFeatureNode(
                            name = label,
                            path = Path(
                                    directories = path,
                                    fileName = Feature.FILE_NAME_WITHOUT_EXTENSION,
                                    fileExtension = Feature.FILE_EXTENSION
                            ),
                            children = children,
                            hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings
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
