package com.testerum.web_backend.services.runner.execution.stopper

import org.slf4j.LoggerFactory
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.util.concurrent.TimeUnit

class ProcessKillerTestExecutionStopper(private val executionId: Long,
                                        process: Process) : TestExecutionStopper {

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
    }

}
