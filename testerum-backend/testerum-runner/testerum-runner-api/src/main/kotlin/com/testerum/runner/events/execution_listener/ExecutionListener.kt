package com.testerum.runner.events.execution_listener

import com.testerum.runner.events.model.RunnerEvent

interface ExecutionListener {

    fun onEvent(event: RunnerEvent)

}