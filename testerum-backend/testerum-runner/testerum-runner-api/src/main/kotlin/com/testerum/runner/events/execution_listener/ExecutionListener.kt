package com.testerum.runner.events.execution_listener

import com.testerum.runner.events.model.RunnerEvent

interface ExecutionListener {

    fun start()

    fun onEvent(event: RunnerEvent)

    fun stop()

}
