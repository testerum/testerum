package com.testerum.model.step.tree.builder

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.model.step.tree.ComposedStepNode
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class ComposedStepDirectoryTreeBuilder {

    private val builder = TreeBuilder(ComposedStepDirectoryTreeBuilderCustomizer)

    fun addComposedStepDirectory(path: List<String>): Unit = builder.add(path)

    fun build(): ComposedContainerStepNode = builder.build() as ComposedContainerStepNode

    override fun toString(): String = builder.toString()

    private object ComposedStepDirectoryTreeBuilderCustomizer : TreeBuilderCustomizer {
        @Suppress("UNCHECKED_CAST")
        override fun getPath(payload: Any): List<String> = (payload as List<String>)

        override fun isContainer(payload: Any): Boolean = false

        override fun getRootLabel(): String = "Composed Steps"

        @Suppress("UNCHECKED_CAST")
        override fun getLabel(payload: Any): String = (payload as List<String>).last()

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<ComposedStepNode> = childrenNodes as List<ComposedStepNode>

            return ComposedContainerStepNode(
                    path = Path.EMPTY,
                    hasOwnOrDescendantWarnings = false,
                    name = getRootLabel(),
                    children = children
            )
        }

        override fun createNode(payload: Any?, label: String, path: List<String>, childrenNodes: List<Any>): Any {
            return when (payload) {
                null, is List<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<ComposedStepNode> = childrenNodes as List<ComposedStepNode>

                    ComposedContainerStepNode(
                            path = Path(
                                    directories = path,
                                    fileName = null,
                                    fileExtension = null
                            ),
                            hasOwnOrDescendantWarnings = false,
                            name = label,
                            children = children
                    )
                }
                else -> throw unknownPayloadException(payload)
            }
        }
    }

}