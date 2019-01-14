package com.testerum.runner.statistics_model

data class StatsStatus(val suiteCount: StatsCountByStatus,
                       val testAvg: StatsCountByStatus,
                       val perTagAvg: Map</*tagName:*/String, StatsCountByStatus>)
