package com.testerum.runner_cmdline.events.execution_listeners.junit

import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import java.util.concurrent.LinkedBlockingDeque
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class JUnitExecutionListener : ExecutionListener {

    val eventQueue = LinkedBlockingDeque<RunnerEvent>()

    override fun start() { }

    override fun onEvent(event: RunnerEvent) {
        eventQueue.put(event)
    }

    override fun stop() {}
}
