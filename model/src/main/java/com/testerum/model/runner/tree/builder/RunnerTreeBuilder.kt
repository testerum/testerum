package com.testerum.model.runner.tree.builder

import com.testerum.model.feature.Feature
import com.testerum.model.runner.tree.RunnerNode
import com.testerum.model.runner.tree.RunnerRootNode
import com.testerum.model.test.TestModel
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class RunnerTreeBuilder {

    private val builder = TreeBuilder(RunnerTreeBuilderCustomizer)

    fun addFeature(feature: Feature): Unit = builder.add(feature)

    fun addTest(test: TestModel): Unit = builder.add(test)

    fun build(): RunnerRootNode = builder.build() as RunnerRootNode

    override fun toString(): String = builder.toString()


    private object RunnerTreeBuilderCustomizer : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = when (payload) {
            is Feature -> payload.path.directories
            else         -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is Feature -> true
            else         -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = "Tests"

        override fun getLabel(payload: Any): String = when (payload) {
            is Feature -> payload.path.directories.last()
            is TestModel -> payload.text
            else         -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<RunnerNode> = childrenNodes as List<RunnerNode>

            return RunnerRootNode(
                    name = getRootLabel(),
                    children = children
            )
        }

        override fun createNode(payload: Any?,
                                label: String,
                                path: List<String>,
                                childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            return label;
        }
    }

}