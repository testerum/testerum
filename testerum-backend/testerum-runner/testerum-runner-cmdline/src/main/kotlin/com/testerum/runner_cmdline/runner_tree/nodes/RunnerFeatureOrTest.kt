package com.testerum.runner_cmdline.runner_tree.nodes

import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

abstract class RunnerFeatureOrTest : RunnerTreeNode() {

    abstract fun execute(context: RunnerContext): ExecutionStatus
    abstract fun skip(context: RunnerContext): ExecutionStatus

}
