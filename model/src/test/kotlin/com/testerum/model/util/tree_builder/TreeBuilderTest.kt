package com.testerum.model.util.tree_builder

import com.testerum.common_kotlin.indent
import com.testerum.common_kotlin.withAdditional
import com.testerum.model.infrastructure.path.Path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TreeBuilderTest {

    @Test
    fun test() {
        val builder = TreeBuilder(MyCustomizer)

        builder.add(
            MyFeature("rest", Path.createInstance("owners/create/rest"))
        )
        builder.add(
            MyFeature("owners", Path.createInstance("owners"))
        )
        builder.add(
            MyFeature("create", Path.createInstance("owners/create"))
        )

        builder.add(
            MyTest("Test 1", Path.createInstance("owners/create/ui"))
        )
        builder.add(
            MyTest("Test 2", Path.createInstance("owners/create/ui"))
        )
        builder.add(
            MyTest("Test 3", Path.createInstance("owners/create/ui"))
        )

        val root: MyRootNode = builder.build() as MyRootNode

        assertThat(builder.toString())
            .isEqualTo(
                """|Features
                   |  owners
                   |    create
                   |      rest
                   |      ui
                   |        Test 1
                   |        Test 2
                   |        Test 3
                   |""".trimMargin()
            )

        assertThat(MyNodeRenderer.render(root))
            .isEqualTo(
                """|[R] Features
                   |  [F] owners
                   |    [F] create
                   |      [F] rest
                   |      [F] ui
                   |        [T] Test 1
                   |        [T] Test 2
                   |        [T] Test 3
                   |""".trimMargin()
            )
    }

    // payloads
    private data class MyFeature(
        val label: String,
        val path: Path
    )

    private data class MyTest(
        val label: String,
        val path: Path
    )

    // nodes
    private interface MyNode {
        val label: String
    }

    private interface MyContainerNode : MyNode {
        val children: List<MyNode>
    }

    private data class MyRootNode(
        override val label: String,
        override val children: List<MyNode>
    ) : MyContainerNode

    private data class MyFeatureNode(
        override val label: String,
        override val children: List<MyNode>
    ) : MyContainerNode

    private data class MyTestNode(override val label: String) : MyNode

    // node renderer
    private object MyNodeRenderer {

        fun render(root: MyRootNode): String {
            val result = StringBuilder()

            renderToString(result, root, 0)

            return result.toString()

        }

        private fun renderToString(
            destination: StringBuilder,
            node: MyNode,
            indentLevel: Int
        ) {
            destination.indent(indentLevel, indentPerLevel = 2)

            val labelPrefix: String = when (node) {
                is MyRootNode -> "[R]"
                is MyFeatureNode -> "[F]"
                is MyTestNode -> "[T]"
                else -> throw IllegalArgumentException("unknown node type [${node.javaClass.name}]")
            }

            destination.append("$labelPrefix ${node.label}").append('\n')

            val children: List<MyNode> = if (node is MyContainerNode) {
                node.children
            } else {
                emptyList()
            }

            for (child in children) {
                renderToString(destination, child, indentLevel + 1)
            }
        }

    }

    // customizer
    private object MyCustomizer : TreeBuilderCustomizer {

        override fun getPath(payload: Any): List<String> = when (payload) {
            is MyFeature -> payload.path.directories
            is MyTest -> payload.path.directories.withAdditional(payload.label)
            else -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = "Features"

        override fun getLabel(payload: Any): String {
            return when (payload) {
                is MyFeature -> payload.label
                is MyTest -> payload.label
                else -> throw unknownPayloadException(payload)
            }
        }

        override fun isContainer(payload: Any): Boolean {
            return when (payload) {
                is MyFeature -> true
                is MyTest -> false
                else -> throw unknownPayloadException(payload)
            }
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            return MyRootNode("Features", childrenNodes as List<MyNode>)
        }

        override fun createNode(
            payload: Any?,
            label: String,
            path: List<String>,
            childrenNodes: List<Any>,
            indexInParent: Int
        ): Any {
            @Suppress("UNCHECKED_CAST")
            return when (payload) {
                null, is MyFeature -> return MyFeatureNode(
                    label = label,
                    children = childrenNodes as List<MyNode>
                )
                is MyTest -> MyTestNode(label)
                else -> throw unknownPayloadException(payload)
            }
        }

    }

}

