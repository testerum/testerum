package com.testerum.web_backend.services.runner.execution.stopper

import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.RunnerStoppedEvent
import org.slf4j.LoggerFactory
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.util.concurrent.TimeUnit

class ProcessKillerTestExecutionStopper(private val executionId: Long,
                                        process: Process,
                                        private val eventProcessor: (event: RunnerEvent) -> Unit) : TestExecutionStopper {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProcessKillerTestExecutionStopper::class.java)
    }

    private val systemProcess: SystemProcess = Processes.newStandardProcess(process)

    override fun stopExecution() {
        LOG.info("stopping execution {}", executionId)

        ProcessUtil.destroyGracefullyOrForcefullyAndWait(
                systemProcess,
                2, TimeUnit.SECONDS,
                10, TimeUnit.SECONDS
        )

        eventProcessor(
                RunnerStoppedEvent()
        )
    }

}
