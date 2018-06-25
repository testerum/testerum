package net.qutester.model.test.operation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.test.TestModel

data class UpdateTestModel @JsonCreator constructor(
        @JsonProperty("oldPath") val oldPath: Path,
        @JsonProperty("testModel") val testModel: TestModel)