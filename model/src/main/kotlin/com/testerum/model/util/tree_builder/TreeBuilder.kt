package com.testerum.model.util.tree_builder

import com.testerum.common_kotlin.indent

class TreeBuilder(private val customizer: TreeBuilderCustomizer) {

    private enum class TreeBuilderNodeType {
        ROOT,
        CONTAINER,
        LEAF,
    }

    private val children = mutableListOf<TreeBuilder>()
    private var path: List<String> = emptyList()
    private var payload: Any? = null

    private val childrenByPath = hashMapOf<List<String>, TreeBuilder>()
    init {
        childrenByPath[emptyList()] = this // add root
    }

    private val comparator = run {
        val leafPayloadComparator = customizer.getLeafPayloadComparator()

        Comparator<TreeBuilder> { left, right ->
            if (left.isContainer) {
                if (right.isContainer) {
                    compareValues(left.label, right.label)
                } else {
                    -1
                }
            } else {
                if (right.isContainer) {
                    1
                } else {
                    // neither left, nor right is container
                    // therefore neither has a null payload (since payloads are null only for virtual containers)
                    val leftPayload: Any = left.payload!!
                    val rightPayload: Any = right.payload!!

                    leafPayloadComparator.compare(leftPayload, rightPayload)
                }
            }
        }
    }

    private val sortedChildren: List<TreeBuilder>
        get() = children.sortedWith(comparator)

    private val isRoot: Boolean
        get() = path.isEmpty()

    private val isContainer: Boolean
        get() {
            val payload = this.payload

            return (payload == null)                   // virtual container
                    || customizer.isContainer(payload) // real container
        }

    private val nodeType: TreeBuilderNodeType
        get() = when {
            isRoot      -> TreeBuilderNodeType.ROOT
            isContainer -> TreeBuilderNodeType.CONTAINER
            else        -> TreeBuilderNodeType.LEAF
        }

    private val label: String
        get() = when (nodeType) {
            TreeBuilderNodeType.ROOT -> customizer.getRootLabel()
            TreeBuilderNodeType.CONTAINER -> payload?.let { customizer.getLabel(it) } ?: path.last()
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
        newNode.path = path

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
            customizer.createRootNode(childrenNodes)
        } else {
            customizer.createNode(payload, label, path, childrenNodes, indexInParent)
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

