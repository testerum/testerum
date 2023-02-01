package com.testerum.common_kotlin

import java.time.LocalDateTime
import java.time.ZoneId

val UTC_TIMEZONE = ZoneId.of("UTC")

fun utcCurrentLocalDateTime(): LocalDateTime = LocalDateTime.now(UTC_TIMEZONE)

fun LocalDateTime.localToUtcTimeZone(): LocalDateTime = atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC_TIMEZONE).toLocalDateTime()

fun LocalDateTime.utcToLocalTimeZone(): LocalDateTime = atZone(UTC_TIMEZONE).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

