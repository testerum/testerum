package com.testerum.runner_cmdline.module_di.submodules

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_json.ObjectMapperFactory
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner_cmdline.events.execution_listeners.json_to_console.JsonToConsoleExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.tree_to_console.TreeToConsoleExecutionListener

class RunnerListenersModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    private val treeToConsoleExecutionListener = TreeToConsoleExecutionListener()
    private val jsonToConsoleExecutionListener = JsonToConsoleExecutionListener(
            objectMapper = ObjectMapperFactory.createKotlinObjectMapper()
    )

    val executionListenerFinder = ExecutionListenerFinder(
            treeListener = treeToConsoleExecutionListener,
            jsonListener = jsonToConsoleExecutionListener
    )

}