package net.qutester.model.tree.builder

import net.qutester.model.feature.Feature
import net.qutester.model.infrastructure.path.HasPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.test.TestModel
import net.qutester.model.tree.FeatureTreeNode
import net.qutester.model.tree.RootTreeNode
import net.qutester.model.tree.TestTreeNode
import net.qutester.model.tree.TreeNode

class TreeNodeBuilder {

    private val children = mutableListOf<TreeNodeBuilder>()
    private lateinit var path: List<String>
    private var payload: HasPath? = null

    private val nodesByPath = hashMapOf<List<String> /*directories*/, TreeNodeBuilder>()

    init {
        nodesByPath[emptyList()] = this // add root
    }

    fun addFeature(feature: Feature) = addNode(feature)

    fun addTest(test: TestModel) = addNode(test)

    private fun addNode(payload: HasPath) {
        val node: TreeNodeBuilder = getNodeByPath(payload.path.directories)

        node.payload = payload
    }

    private fun getNodeByPath(path: List<String>): TreeNodeBuilder {
        val existingNode: TreeNodeBuilder? = nodesByPath[path]

        if (existingNode != null) {
            return existingNode
        }

        val parentNode: TreeNodeBuilder = getNodeByPath(path.subList(0, path.size - 1))

        val newNode = TreeNodeBuilder()
        newNode.path = path

        parentNode.children += newNode

        nodesByPath[path] = newNode

        return newNode
    }

    fun build(): RootTreeNode = build(createRoot = true) as RootTreeNode

    private fun build(createRoot: Boolean): TreeNode {
        val payload: HasPath? = this.payload

        // test
        if (payload is TestModel) {
            return TestTreeNode(
                    name = payload.text,
                    path = payload.path,
                    properties = payload.properties,
                    hasOwnOrDescendantWarnings = payload.hasOwnOrDescendantWarnings
            )
        }

        val childrenNodes: List<TreeNode> = children.map { it.build(createRoot = false) }
                                                    .sortedBy { it.name }

        val hasOwnOrDescendantWarnings: Boolean = childrenNodes.any { it.hasOwnOrDescendantWarnings }

        // feature
        if (payload != null) {
            val feature: Feature = payload as Feature // since it's not a test, it MUST be a feature

            return FeatureTreeNode(
                    name = feature.path.directories.last(),
                    path = feature.path,
                    children = childrenNodes,
                    hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings
            )
        }

        // payload is null: this is the root, or a feature that doesn't exist
        if (createRoot) {
            return RootTreeNode(
                    name = "Features",
                    children = childrenNodes,
                    hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings
            )
        } else {
            return FeatureTreeNode(
                    name = path.last(),
                    path = Path(
                            directories = path,
                            fileName = Feature.FILE_NAME_WITHOUT_EXTENSION,
                            fileExtension = Feature.FILE_EXTENSION
                    ),
                    children = childrenNodes,
                    hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings
            )
        }
    }

    override fun toString(): String {
        val result = StringBuilder()

        appendToString(result, 0)

        return result.toString()
    }

    private fun appendToString(destination: StringBuilder, indent: Int) {
        destination.indent(indent)

        destination.append(this.payload?.toString() ?: "{null}").append('\n')

        for (child in children) {
            child.appendToString(destination, indent + 1)
        }
    }

    private fun StringBuilder.indent(indent: Int): StringBuilder {
        for (i in 1..indent) {
            append("    ")
        }

        return this
    }
}