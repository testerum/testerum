package net.qutester.model.manual.operation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.manual.ManualTest
import net.qutester.model.test.TestModel

data class UpdateManualTestModel @JsonCreator constructor(
        @JsonProperty("oldPath") val oldPath: Path,
        @JsonProperty("manualTest") val manualTest: ManualTest)