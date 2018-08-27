package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class ManualExecPlans @JsonCreator constructor(@JsonProperty("activeExecPlans") val activeExecPlans: List<ManualExecPlan> = emptyList(),
                                                    @JsonProperty("finalizedExecPlans") val finalizedExecPlans: List<ManualExecPlan> = emptyList())
