package com.testerum.runner.statistics_model

import com.testerum.common_stats.avg.Avg

data class StatsAll(val status: StatsStatus,
                    val suiteAvgDurationMillis: Avg)
