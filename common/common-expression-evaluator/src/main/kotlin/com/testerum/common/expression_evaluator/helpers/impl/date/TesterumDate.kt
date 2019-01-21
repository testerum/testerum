package com.testerum.common.expression_evaluator.helpers.impl.date

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TesterumDate {

    private val defaultDateToStringPattern = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS")
    private val date: LocalDateTime

    constructor(date: LocalDateTime) {
        this.date = date
    }

    constructor() {
        date = LocalDateTime.now()
    }

    constructor(year: Int, month: Int, dayOfMonth: Int) {
        date = LocalDateTime.of(year, month, dayOfMonth, 0, 0, 0)
    }

    constructor(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int) {
        date = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second)
    }

    constructor(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int, nanoOfSecond: Int) {
        date = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond)
    }

    fun minusYears(years: Long): TesterumDate {
        return TesterumDate(
                date.minusYears(years)
        )
    }

    fun plusYears(years: Long): TesterumDate {
        return TesterumDate(
                date.plusYears(years)
        )
    }


    fun minusMonths(months: Long): TesterumDate {
        return TesterumDate(
                date.minusMonths(months)
        )
    }

    fun plusMonths(months: Long): TesterumDate {
        return TesterumDate(
                date.plusMonths(months)
        )
    }

    fun minusDays(days: Long): TesterumDate {
        return TesterumDate(
                date.minusDays(days)
        )
    }

    fun plusDays(days: Long): TesterumDate {
        return TesterumDate(
                date.plusDays(days)
        )
    }

    fun minusHours(hours: Long): TesterumDate {
        return TesterumDate(
                date.minusHours(hours)
        )
    }

    fun plusHours(hours: Long): TesterumDate {
        return TesterumDate(
                date.plusHours(hours)
        )
    }

    fun minusMinutes(minutes: Long): TesterumDate {
        return TesterumDate(
                date.minusMinutes(minutes)
        )
    }

    fun plusMinutes(minutes: Long): TesterumDate {
        return TesterumDate(
                date.plusMinutes(minutes)
        )
    }

    fun minusSeconds(seconds: Long): TesterumDate {
        return TesterumDate(
                date.minusSeconds(seconds)
        )
    }

    fun plusSeconds(seconds: Long): TesterumDate {
        return TesterumDate(
                date.plusSeconds(seconds)
        )
    }

    fun minusNanos(nanos: Long): TesterumDate {
        return TesterumDate(
                date.minusNanos(nanos)
        )
    }

    fun plusNanos(nanos: Long): TesterumDate {
        return TesterumDate(
                date.plusNanos(nanos)
        )
    }

    override fun toString(): String {
        return date.format(
                defaultDateToStringPattern
        )
    }

    fun toString(format: String): String {
        return date.format(
                DateTimeFormatter.ofPattern(format)
        )
    }
}
