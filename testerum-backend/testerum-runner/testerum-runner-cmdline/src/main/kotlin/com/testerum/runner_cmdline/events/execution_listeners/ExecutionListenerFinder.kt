package com.testerum.runner_cmdline.events.execution_listeners

import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.events.execution_listeners.json_to_console.JsonToConsoleExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.tree_to_console.TreeToConsoleExecutionListener

class ExecutionListenerFinder(private val treeListener: TreeToConsoleExecutionListener,
                              private val jsonListener: JsonToConsoleExecutionListener) {


    lateinit var outputFormat: CmdlineParams.OutputFormat

    fun findExecutionListener(): ExecutionListener
        = when(outputFormat) {
            CmdlineParams.OutputFormat.TREE -> treeListener
            CmdlineParams.OutputFormat.JSON -> jsonListener
        }

}