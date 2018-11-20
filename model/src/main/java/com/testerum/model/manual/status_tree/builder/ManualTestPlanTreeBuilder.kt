package com.testerum.model.manual.status_tree.builder

import com.testerum.common_kotlin.withAdditional
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeBase
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeContainer
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeNode
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeRoot
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class ManualTestPlanTreeBuilder(private val testPlanName: String) {

    private val builder = TreeBuilder(
            ManualTestPlanTreeBuilderCustomizer(testPlanName)
    )

    fun addTest(test: ManualTest) = builder.add(test)

    fun build(): ManualTestsStatusTreeRoot = builder.build() as ManualTestsStatusTreeRoot

    override fun toString(): String = builder.toString()

    private class ManualTestPlanTreeBuilderCustomizer(private val testPlanName: String) : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = (payload as ManualTest).path.directories.withAdditional(getLabel(payload))

        override fun isContainer(payload: Any): Boolean = false

        override fun getRootLabel(): String = testPlanName

        override fun getLabel(payload: Any): String = (payload as ManualTest).name

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children = childrenNodes as List<ManualTestsStatusTreeBase>

            // todo: discuss with Ionut and implement this
            val status = ManualTestStatus.NOT_APPLICABLE

            return ManualTestsStatusTreeRoot(
                    path = Path.EMPTY,
                    name = getRootLabel(),
                    children = children,
                    status = status
            )
        }

        override fun createNode(payload: Any?,
                                label: String,
                                path: List<String>,
                                childrenNodes: List<Any>,
                                indexInParent: Int): Any {
            return when (payload) {
                null -> {
                    @Suppress("UNCHECKED_CAST")
                    val children = childrenNodes as List<ManualTestsStatusTreeBase>

                    // todo: discuss with Ionut and implement this
                    val status = ManualTestStatus.NOT_APPLICABLE

                    ManualTestsStatusTreeContainer(
                            path = Path(directories = path),
                            name = label,
                            status = status,
                            children = children
                    )
                }
                is ManualTest -> ManualTestsStatusTreeNode(
                        path = payload.path,
                        name = label,
                        status = payload.status
                )
                else -> throw unknownPayloadException(payload)
            }

        }
    }
}