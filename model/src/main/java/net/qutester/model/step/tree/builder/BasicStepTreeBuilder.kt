package net.qutester.model.step.tree.builder

import com.testerum.common_kotlin.withAdditional
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.step.BasicStepDef
import net.qutester.model.step.tree.BasicContainerStepNode
import net.qutester.model.step.tree.BasicStepNode
import net.qutester.model.step.tree.BasicStepStepNode
import net.qutester.util.tree_builder.TreeBuilder
import net.qutester.util.tree_builder.TreeBuilderCustomizer

class BasicStepTreeBuilder {

    private val builder = TreeBuilder(BasicStepTreeBuilderCustomizer)

    fun addBasicStepDef(basicStepDef: BasicStepDef): Unit = builder.add(basicStepDef)

    fun build(): BasicContainerStepNode = builder.build() as BasicContainerStepNode

    override fun toString(): String = builder.toString()

    private object BasicStepTreeBuilderCustomizer : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = (payload as BasicStepDef).path.directories.withAdditional(getLabel(payload))

        override fun isContainer(payload: Any): Boolean = false

        override fun getRootLabel(): String = "Basic Steps"

        override fun getLabel(payload: Any): String = (payload as BasicStepDef).toString()

        override fun getLeafPayloadComparator(): Comparator<Any> = compareBy {
            val stepDef: BasicStepDef = it as BasicStepDef

            // using ordinal because we want the order in the enum (GIVEN, WHEN, THEN)
            // the lexicographic order is wrong (GIVEN, THEN, WHEN)
            stepDef.phase.ordinal.toString() + getLabel(it)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<BasicStepNode> = childrenNodes as List<BasicStepNode>

            val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

            return BasicContainerStepNode(
                    path = Path.EMPTY,
                    hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings,
                    name = getRootLabel(),
                    children = children
            )
        }

        override fun createNode(payload: Any?, label: String, path: List<String>, childrenNodes: List<Any>): Any {
            return when (payload) {
                null -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<BasicStepNode> = childrenNodes as List<BasicStepNode>

                    val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

                    BasicContainerStepNode(
                            path = Path(
                                    directories = path,
                                    fileName = null,
                                    fileExtension = null
                            ),
                            hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings,
                            name = label,
                            children = children
                    )
                }
                is BasicStepDef -> BasicStepStepNode(
                        path = payload.path,
                        hasOwnOrDescendantWarnings = payload.hasOwnOrDescendantWarnings,
                        stepDef = payload
                )
                else -> throw unknownPayloadException(payload)
            }
        }
    }
}