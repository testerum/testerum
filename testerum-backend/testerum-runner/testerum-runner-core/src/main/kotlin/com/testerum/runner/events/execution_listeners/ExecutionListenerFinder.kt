package com.testerum.runner.events.execution_listeners

import com.testerum.runner.cmdline.params.model.CmdlineParams
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.execution_listeners.json_to_console.JsonToConsoleExecutionListener
import com.testerum.runner.events.execution_listeners.tree_to_console.TreeToConsoleExecutionListener
import kotlin.reflect.full.memberProperties

class ExecutionListenerFinder(private val treeListener: TreeToConsoleExecutionListener,
                              private val jsonListener: JsonToConsoleExecutionListener) {

    companion object {
        private val VALID_OUTPUT_FORMATS: List<String> = run {
            CmdlineParams.OutputFormat::class.memberProperties.map { it.call() as String }
        }
    }

    lateinit var outputFormat: String

    fun findExecutionListener(): ExecutionListener
        = when(outputFormat) {
            CmdlineParams.OutputFormat.TREE -> treeListener
            CmdlineParams.OutputFormat.JSON    -> jsonListener
            else                               -> throw RuntimeException("unknown outputFormat [$outputFormat]; valid values are: $VALID_OUTPUT_FORMATS")
        }

}