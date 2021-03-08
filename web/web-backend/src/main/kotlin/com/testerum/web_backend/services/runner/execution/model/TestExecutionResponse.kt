package com.testerum.web_backend.services.runner.execution.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.runner.old_tree.RunnerRootNode

data class TestExecutionResponse @JsonCreator constructor(@JsonProperty("executionId")    val executionId: Long,
                                                          @JsonProperty("runnerRootNode") val runnerRootNode: RunnerRootNode)
