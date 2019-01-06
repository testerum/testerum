package com.testerum.common.expression_evaluator.helpers.impl.date

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TesterumDate {

    private val date: LocalDateTime = LocalDateTime.now();
    private var formatter: DateTimeFormatter? = null;

    fun minusYears(years: Long): TesterumDate {
        date.minusYears(years)
        return this;
    }

    fun minusMonths(months: Long): TesterumDate {
        date.minusMonths(months)
        return this;
    }

    fun minusWeeks(weeks: Long): TesterumDate {
        date.minusWeeks(weeks)
        return this;
    }

    fun minusHours(hours: Long): TesterumDate {
        date.minusHours(hours)
        return this;
    }

    fun minusMinutes(minutes: Long): TesterumDate {
        date.minusMinutes(minutes)
        return this;
    }

    fun minusSeconds(seconds: Long): TesterumDate {
        date.minusSeconds(seconds)
        return this;
    }

    fun plusYears(years: Long): TesterumDate {
        date.plusYears(years)
        return this;
    }

    fun plusMonths(months: Long): TesterumDate {
        date.plusMonths(months)
        return this;
    }

    fun plusWeeks(weeks: Long): TesterumDate {
        date.plusWeeks(weeks)
        return this;
    }

    fun plusHours(hours: Long): TesterumDate {
        date.plusHours(hours)
        return this;
    }

    fun plusMinutes(minutes: Long): TesterumDate {
        date.plusMinutes(minutes)
        return this;
    }

    fun plusSeconds(seconds: Long): TesterumDate {
        date.plusSeconds(seconds)
        return this;
    }

    fun format(format: String): TesterumDate {
        this.formatter = DateTimeFormatter.ofPattern(format)
        return this;
    }

    override fun toString(): String {
        if (this.formatter == null) {
            return date.toString()
        }

        return date.format(formatter)
    }
}