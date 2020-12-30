package com.testerum.model.step.tree.builder

import com.testerum.common_kotlin.withAdditional
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.model.step.tree.ComposedStepNode
import com.testerum.model.step.tree.ComposedStepStepNode
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class ComposedStepTreeBuilder {

    private val builder = TreeBuilder(ComposedStepTreeBuilderCustomizer)

    fun addComposedStepDef(composedStepDef: ComposedStepDef): Unit = builder.add(composedStepDef)

    fun build(): ComposedContainerStepNode = builder.build() as ComposedContainerStepNode

    override fun toString(): String = builder.toString()

    private object ComposedStepTreeBuilderCustomizer : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = (payload as ComposedStepDef).path.directories.withAdditional(getLabel(payload))

        override fun isContainer(payload: Any): Boolean = false

        override fun getRootLabel(): String = "Composed Steps"

        override fun getLabel(payload: Any): String = (payload as ComposedStepDef).toString()

        override fun createRootNode(payload: Any?, childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<ComposedStepNode> = childrenNodes as List<ComposedStepNode>

            val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

            return ComposedContainerStepNode(
                    path = Path.EMPTY,
                    hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings,
                    name = getRootLabel(),
                    children = children
            )
        }

        override fun createNode(payload: Any?, label: String, path: List<String>, childrenNodes: List<Any>, indexInParent: Int): Any {
            return when (payload) {
                null -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<ComposedStepNode> = childrenNodes as List<ComposedStepNode>

                    val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

                    ComposedContainerStepNode(
                            path = Path(
                                    directories = path,
                                    fileName = null,
                                    fileExtension = null
                            ),
                            hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings,
                            name = label,
                            children = children
                    )
                }
                is ComposedStepDef -> ComposedStepStepNode(
                        path = payload.path,
                        hasOwnOrDescendantWarnings = payload.hasOwnOrDescendantWarnings,
                        stepDef = payload
                )
                else -> throw unknownPayloadException(payload)
            }
        }
    }

}
