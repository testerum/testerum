package net.qutester.model.step_tree.builder

import com.testerum.common_kotlin.withAdditional
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.step_tree.ComposedContainerStepNode
import net.qutester.model.step_tree.ComposedStepNode
import net.qutester.model.step_tree.ComposedStepStepNode
import net.qutester.util.tree_builder.TreeBuilder
import net.qutester.util.tree_builder.TreeBuilderCustomizer

class ComposedStepTreeBuilder {

    private val builder = TreeBuilder(ComposedStepTreeBuilderCustomizer)

    fun addComposedStepDef(composedStepDef: ComposedStepDef): Unit = builder.add(composedStepDef)

    fun build(): ComposedContainerStepNode = builder.build() as ComposedContainerStepNode

    override fun toString(): String = builder.toString()

    private object ComposedStepTreeBuilderCustomizer : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = (payload as ComposedStepDef).path.directories.withAdditional(getLabel(payload))

        override fun isContainer(payload: Any): Boolean = false

        override fun getRootLabel(): String = "Composed Steps"

        override fun getLabel(payload: Any): String = (payload as ComposedStepDef).toString()

        override fun getLeafPayloadComparator(): Comparator<Any> = compareBy {
            val stepDef: ComposedStepDef = it as ComposedStepDef

            // using ordinal because we want the order in the enum (GIVEN, WHEN, THEN)
            // the lexicographic order is wrong (GIVEN, THEN, WHEN)
            stepDef.phase.ordinal.toString() + getLabel(it)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<ComposedStepNode> = childrenNodes as List<ComposedStepNode>

            val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

            return ComposedContainerStepNode(
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
                    val children: List<ComposedStepNode> = childrenNodes as List<ComposedStepNode>

                    val hasOwnOrDescendantWarnings: Boolean = children.any { it.hasOwnOrDescendantWarnings }

                    ComposedContainerStepNode(
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
                is ComposedStepDef -> ComposedStepStepNode(
                        path = payload.path,
                        hasOwnOrDescendantWarnings = payload.hasOwnOrDescendantWarnings,
                        phase = payload.phase,
                        stepPattern = payload.stepPattern
                )
                else -> throw unknownPayloadException(payload)
            }
        }
    }

}