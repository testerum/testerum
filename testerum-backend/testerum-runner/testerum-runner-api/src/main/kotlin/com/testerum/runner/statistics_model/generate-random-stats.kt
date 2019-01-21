package com.testerum.runner.statistics_model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_stats.avg.Avg
import java.time.LocalDate
import java.util.*
import java.util.concurrent.ThreadLocalRandom

fun main(args: Array<String>) {
    val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(AfterburnerModule())
        registerModule(JavaTimeModule())
        registerModule(GuavaModule())

        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    println(
            OBJECT_MAPPER.writeValueAsString(
                    randomStats()
            )
    )
}

private fun randomStats(): Stats {
    val perDay = TreeMap<LocalDate, StatsAll>()

    var date = LocalDate.now().minusDays(45)

    for (i in 1..40) {
        perDay[date] = randomStatsAll()
        date = date.plusDays(1)
    }

    return Stats(perDay)
}

private fun randomStatsAll(): StatsAll {
    val avgDuration = ThreadLocalRandom.current().nextLong(60_000, 10_000_000)
    val count = ThreadLocalRandom.current().nextLong(1, 5)

    return StatsAll(
            status = randomStatsStatus(),
            suiteAvgDurationMillis = Avg(
                    sum = avgDuration * count,
                    count = count
            )
    )
}

private fun randomStatsStatus(): StatsStatus {
    val tags = setOf(
        "Guest Token",
        "Login",
        "Reservation API",
        "Flight Offer API",
        "Flight Status API",
        "Location City Search",
        "Customer Profile",
        "Mobile API",
        "Seats offer Reservation",
        "Bag offer Reservation",
        "Meals offer Reservation",
        "Check-in API",
        "Seats offer Check-in",
        "Bag offer Check-in",
        "Flying Blue Dashboard",
        "Flying Blue Card Image",
        "Flying Blue Transactions",
        "Flying Blue Benefits"
    )

    val perTagAvg = TreeMap<String, StatsCountByStatus>()
    for (tag in tags) {
        perTagAvg[tag] = randomStatsCountByStatus()
    }

    return StatsStatus(
            suiteCount = randomStatsCountByStatus(),
            testAvg = randomStatsCountByStatus(),
            perTagAvg = perTagAvg
    )
}

private fun randomStatsCountByStatus(): StatsCountByStatus {
    val countByStatusMap = TreeMap<ExecutionStatus, Long>()

    for (executionStatus in ExecutionStatus.values()) {
        val value = ThreadLocalRandom.current().nextLong(0, 200)

        countByStatusMap[executionStatus] = value
    }

    return StatsCountByStatus(countByStatusMap)
}
