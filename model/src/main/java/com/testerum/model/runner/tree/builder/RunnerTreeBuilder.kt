package com.testerum.model.runner.tree.builder

import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.tree.*
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class RunnerTreeBuilder {

    val builder = TreeBuilder(RunnerTreeBuilderCustomizer)

    fun addTest(test: TestModel) = builder.add(test)

    fun build(): RunnerRootNode = builder.build() as RunnerRootNode

    private object RunnerTreeBuilderCustomizer : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = when (payload) {
            is TestModel -> payload.path.parts
            else         -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is TestModel -> false
            else         -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = "Runner"

        override fun getLabel(payload: Any): String = when (payload) {
            is TestModel -> payload.text
            else         -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<RunnerTestOrFeatureNode> = childrenNodes as List<RunnerTestOrFeatureNode>

            return RunnerRootNode(
                    name = getRootLabel(),
                    children = children
            )
        }

        override fun createNode(payload: Any?, label: String, path: List<String>, childrenNodes: List<Any>, indexInParent: Int): Any {
            @Suppress("UNCHECKED_CAST")
            return when (payload) {
                null -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<RunnerTestOrFeatureNode> = childrenNodes as List<RunnerTestOrFeatureNode>

                    val featurePath = Path(
                            directories = path,
                            fileName = Feature.FILE_NAME_WITHOUT_EXTENSION,
                            fileExtension = Feature.FILE_EXTENSION
                    )
                    RunnerFeatureNode(
                            id = featurePath.toString(),
                            name = label,
                            path = featurePath,
                            children = children
                    )
                }
                is TestModel -> {
                    val testChildren: List<RunnerStepNode> = payload.stepCalls.map(this::createStepCallBranch)



                    RunnerTestNode(
                            id = payload.id,
                            name = label,
                            path = payload.path,
                            children = testChildren
                    )
                }
                else -> throw unknownPayloadException(payload)
            }
        }

        private fun createStepCallBranch(stepCall: StepCall): RunnerStepNode {
            val stepDef = stepCall.stepDef
            val id = stepCall.id
            val path = stepDef.path

            return when (stepDef) {
                is UndefinedStepDef -> RunnerUndefinedStepNode(
                        id = id,
                        path = path,
                        stepCall = stepCall
                )
                is BasicStepDef -> RunnerBasicStepNode(
                        id = id,
                        path = path,
                        stepCall = stepCall
                )
                is ComposedStepDef -> {
                    val childrenNodes: List<RunnerStepNode> = stepDef.stepCalls.map(this::createStepCallBranch)

                    RunnerComposedStepNode(
                            id = id,
                            path = path,
                            stepCall = stepCall,
                            children = childrenNodes
                    )
                }
                else -> throw IllegalArgumentException("unknown step call type [${stepCall.javaClass.name}]")
            }
        }
    }

}
