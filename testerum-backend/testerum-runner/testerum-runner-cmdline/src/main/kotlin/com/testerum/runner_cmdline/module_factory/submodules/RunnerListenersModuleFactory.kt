package com.testerum.runner_cmdline.module_factory.submodules

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_json.ObjectMapperFactory
import com.testerum.runner_cmdline.events.execution_listeners.ExecutionListenerFinder
import com.testerum.runner_cmdline.events.execution_listeners.json_to_console.JsonToConsoleExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.tree_to_console.TreeToConsoleExecutionListener

@Suppress("unused", "LeakingThis")
class RunnerListenersModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    val treeToConsoleExecutionListener = TreeToConsoleExecutionListener()
    val jsonToConsoleExecutionListener = JsonToConsoleExecutionListener(
            objectMapper = ObjectMapperFactory.createKotlinObjectMapper()
    )

    val executionListenerFinder = ExecutionListenerFinder(
            treeListener = treeToConsoleExecutionListener,
            jsonListener = jsonToConsoleExecutionListener
    )

}