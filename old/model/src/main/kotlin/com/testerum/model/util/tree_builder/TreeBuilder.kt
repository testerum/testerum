package com.testerum.model.util.tree_builder

import com.testerum.common_kotlin.indent
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.new_tree_builder.PathBasedTreeBuilder

class TreeBuilder(private val customizer: TreeBuilderCustomizer) : HasPath {

    private enum class TreeBuilderNodeType {
        ROOT,
        CONTAINER,
        LEAF,
    }

    private val children = mutableListOf<TreeBuilder>()
    private var pathList: List<String> = emptyList()
    private var payload: Any? = null

    override val path
        get() = Path.createInstance(pathList.joinToString(separator = "/"))

    private val childrenByPath = hashMapOf<List<String>, TreeBuilder>()

    init {
        childrenByPath[emptyList()] = this // add root
    }

    private val sortedChildren: List<TreeBuilder>
        get() = children.sortedWith(PathBasedTreeBuilder.NODES_COMPARATOR)

    private val isRoot: Boolean
        get() = pathList.isEmpty()

    private val isContainer: Boolean
        get() {
            val payload = this.payload

            return (payload == null)                   // virtual container
                || customizer.isContainer(payload) // real container
        }

    private val nodeType: TreeBuilderNodeType
        get() = when {
            isRoot -> TreeBuilderNodeType.ROOT
            isContainer -> TreeBuilderNodeType.CONTAINER
            else -> TreeBuilderNodeType.LEAF
        }

    private val label: String
        get() = when (nodeType) {
            TreeBuilderNodeType.ROOT -> customizer.getRootLabel()
            TreeBuilderNodeType.CONTAINER -> payload?.let { customizer.getLabel(it) } ?: pathList.last()
            TreeBuilderNodeType.LEAF -> customizer.getLabel(payload!!)
        }


    fun add(payload: Any) {
        val path = customizer.getPath(payload)

        val node = getOrCreateNode(path)

        node.payload = payload
    }

    private fun getOrCreateNode(path: List<String>): TreeBuilder {
        val existingNode: TreeBuilder? = childrenByPath[path]

        if (existingNode != null) {
            return existingNode
        }

        val parentNode: TreeBuilder = getOrCreateNode(path.subList(0, path.size - 1))

        val newNode = TreeBuilder(customizer)
        newNode.pathList = path

        parentNode.children += newNode

        childrenByPath[path] = newNode

        return newNode
    }

    fun build(): Any = build(indexInParent = -1)

    private fun build(indexInParent: Int): Any {
        // first, recursively build children
        val childrenNodes = mutableListOf<Any>()
        for ((index, child) in sortedChildren.withIndex()) {
            childrenNodes += child.build(index)
        }

        // then, return the actual node
        return if (this.isRoot) {
            customizer.createRootNode(payload, childrenNodes)
        } else {
            customizer.createNode(payload, label, pathList, childrenNodes, indexInParent)
        }
    }

    override fun toString(): String {
        val result = StringBuilder()

        appendToString(result, 0)

        return result.toString()
    }

    private fun appendToString(destination: StringBuilder, indent: Int) {
        destination.indent(indent, indentPerLevel = 2)

        destination.append(this.label).append('\n')

        for (child in sortedChildren) {
            child.appendToString(destination, indent + 1)
        }
    }

}

