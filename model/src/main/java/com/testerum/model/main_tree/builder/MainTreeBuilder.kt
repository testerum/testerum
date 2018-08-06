package com.testerum.model.main_tree.builder

import com.testerum.common_kotlin.withAdditional
import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.main_tree.FeatureMainNode
import com.testerum.model.main_tree.MainNode
import com.testerum.model.main_tree.RootMainNode
import com.testerum.model.main_tree.TestMainNode
import com.testerum.model.test.TestModel
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class MainTreeBuilder {

    private val builder = TreeBuilder(MainTreeBuilderCustomizer)

    fun addFeature(feature: Feature): Unit = builder.add(feature)

    fun addTest(test: TestModel): Unit = builder.add(test)

    fun build(): RootMainNode = builder.build() as RootMainNode

    override fun toString(): String = builder.toString()


    private object MainTreeBuilderCustomizer : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = when (payload) {
            is Feature -> payload.path.directories
            is TestModel -> payload.path.directories.withAdditional(getLabel(payload))
            else         -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is Feature -> true
            is TestModel -> false
            else         -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = "Features"

        override fun getLabel(payload: Any): String = when (payload) {
            is Feature -> payload.path.directories.last()
            is TestModel -> payload.text
            else         -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<MainNode> = childrenNodes as List<MainNode>

            val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

            return RootMainNode(
                    name = getRootLabel(),
                    children = children,
                    hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings
            )
        }

        override fun createNode(payload: Any?,
                                label: String,
                                path: List<String>,
                                childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            return when (payload) {
                null, is Feature -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<MainNode> = childrenNodes as List<MainNode>

                    val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

                    FeatureMainNode(
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
                is TestModel -> TestMainNode(
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