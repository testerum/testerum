package net.qutester.model.manual.runner.operation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.manual.ManualTest
import net.qutester.model.manual.runner.ManualTestExe
import net.qutester.model.manual.runner.ManualTestsRunner
import net.qutester.model.test.TestModel

data class UpdateManualTestExecutionModel @JsonCreator constructor(
        @JsonProperty("manualTestRunnerPath") val manualTestRunnerPath: Path,
        @JsonProperty("manualTestExe") val manualTest: ManualTestExe)