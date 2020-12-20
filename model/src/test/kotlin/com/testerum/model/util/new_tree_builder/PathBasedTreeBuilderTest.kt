package com.testerum.model.util.new_tree_builder

import com.testerum.common_kotlin.indent
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PathBasedTreeBuilderTest {

    @Test
    fun test() {
        val builder = PathBasedTreeBuilder(MyTreeNodeFactory)

        val root: MyRootNode = builder.createTree(
            listOf(
                MyFeature("Root feature", Path.createInstance("")),
                MyTest("Root test", Path.createInstance("root-test.test")),
                MyFeature("rest", Path.createInstance("owners/create/rest")),
                MyFeature("owners", Path.createInstance("owners")),
                MyFeature("create", Path.createInstance("owners/create")),
                MyTest("Test", Path.createInstance("owners/create/ui/t.test")),
                MyTest("Test zoo", Path.createInstance("owners/create/ui/t zoo.test")),
                MyTest("Test add", Path.createInstance("owners/create/ui/t add.test")),
            )
        )

        assertThat(MyNodeRenderer.render(root))
            .isEqualTo(
                """|[R] Root feature, parentLabel=[null]
                   |  [F] owners, parentLabel=[Root feature]
                   |    [F] create, parentLabel=[owners]
                   |      [F] rest, parentLabel=[create]
                   |      [F] ui, parentLabel=[create]
                   |        [T] Test, parentLabel=[ui]
                   |        [T] Test add, parentLabel=[ui]
                   |        [T] Test zoo, parentLabel=[ui]
                   |  [T] Root test, parentLabel=[Root feature]
                   |""".trimMargin()
            )
    }

    // items
    private data class MyFeature(
        val label: String,
        override val path: Path
    ) : HasPath

    private data class MyTest(
        val label: String,
        override val path: Path
    ) : HasPath

    // nodes
    private interface MyNode : TreeNode {
        val parent: MyNode?
        val label: String
    }

    private abstract class MyContainerNode(override val parent: MyNode?) : MyNode, ContainerTreeNode {
        protected val _children = mutableListOf<MyNode>()

        val children: List<MyNode>
            get() = _children

        override val childrenCount: Int
            get() = _children.size

        override fun addChild(child: TreeNode) {
            _children += child as? MyNode
                ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${child.javaClass}]: [$child]")
        }
    }

    private class MyRootNode(
        override val label: String,
    ) : MyContainerNode(null)

    private class MyFeatureNode(
        parent: MyNode,
        override val label: String,
    ) : MyContainerNode(parent)

    private data class MyTestNode(
        override val parent: MyNode,
        override val label: String
    ) : MyNode

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

            destination.append("$labelPrefix ${node.label}").append(", parentLabel=[").append(node.parent?.label).append("]").append('\n')

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

    private object MyTreeNodeFactory : TreeNodeFactory<MyRootNode, MyFeatureNode> {
        override fun createRootNode(item: HasPath?): MyRootNode {
            val label = if (item != null) {
                (item as MyFeature).label
            } else {
                "Root"
            }

            return MyRootNode(label)
        }

        override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): MyFeatureNode {
            return MyFeatureNode(
                parent = parentNode as MyNode,
                label = path.parts.last(),
            )
        }

        override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
            return when (item) {
                is MyFeature -> return MyFeatureNode(
                    parent = parentNode as MyNode,
                    label = item.label,
                )
                is MyTest -> MyTestNode(
                    parent = parentNode as MyNode,
                    label = item.label
                )
                else -> throw IllegalArgumentException("unexpected item type [${item.javaClass}]: [$item]")
            }
        }
    }

}
