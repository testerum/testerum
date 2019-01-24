package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ManualTestPlans @JsonCreator constructor(@JsonProperty("activeTestPlans") val activeTestPlans: List<ManualTestPlan> = emptyList(),
                                                    @JsonProperty("finalizedTestPlans") val finalizedTestPlans: List<ManualTestPlan> = emptyList())
