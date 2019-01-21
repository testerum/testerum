package com.testerum.model.run_result

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RunnerResultsInfo @JsonCreator constructor(@JsonProperty("reportDirs") val reportDirs: List<RunnerResultsDirInfo>,
                                                      @JsonProperty("statisticsUrl") val statisticsUrl: String?)
