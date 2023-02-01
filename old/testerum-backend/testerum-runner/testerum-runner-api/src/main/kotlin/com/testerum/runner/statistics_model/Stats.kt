package com.testerum.runner.statistics_model

import java.time.LocalDate

data class Stats(val perDay: Map<LocalDate, StatsAll>)
