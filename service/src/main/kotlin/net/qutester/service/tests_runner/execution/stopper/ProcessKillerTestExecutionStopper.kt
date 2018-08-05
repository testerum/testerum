package net.qutester.service.tests_runner.execution.stopper

import org.slf4j.LoggerFactory
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.util.concurrent.TimeUnit

class ProcessKillerTestExecutionStopper(private val executionId: Long,
                                        private val process: Process) : TestExecutionStopper {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ProcessKillerTestExecutionStopper::class.java)
    }

    override fun stopExecution() {
        LOGGER.info("stopping execution {}", executionId)

        val systemProcess: SystemProcess = Processes.newStandardProcess(process)
        ProcessUtil.destroyGracefullyOrForcefullyAndWait(
                systemProcess,
                2, TimeUnit.SECONDS,
                10, TimeUnit.SECONDS
        )
    }

}