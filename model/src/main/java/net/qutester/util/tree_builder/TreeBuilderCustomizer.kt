package net.qutester.util.tree_builder

interface TreeBuilderCustomizer {

    fun getPath(payload: Any): List<String>
    fun isContainer(payload: Any): Boolean
    fun getRootLabel(): String
    fun getLabel(payload: Any): String

    fun getLeafPayloadComparator(): Comparator<Any> {
        return compareBy { getLabel(it) }
    }

    fun createRootNode(childrenNodes: List<Any>): Any
    fun createNode(payload: Any?, label: String, path: List<String>, childrenNodes: List<Any>): Any

    fun unknownPayloadException(payload: Any): Throwable = IllegalArgumentException("unknown payload type [${payload.javaClass.name}]")

}