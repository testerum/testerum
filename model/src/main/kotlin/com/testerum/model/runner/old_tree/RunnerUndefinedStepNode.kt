package com.testerum.model.runner.old_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.StepCall

data class RunnerUndefinedStepNode @JsonCreator constructor(@JsonProperty("id") override val id: String,
                                                            @JsonProperty("path") override val path: Path,
                                                            @JsonProperty("stepCall") override val stepCall: StepCall) : RunnerStepNode
