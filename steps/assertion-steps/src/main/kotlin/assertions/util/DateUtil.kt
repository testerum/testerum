package assertions.util

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

private const val DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private val SIMPLE_DATE_FORMAT =  SimpleDateFormat(DATE_TIME_PATTERN)
private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).withZone(ZoneId.of("UTC"))

fun Date.toIsoString(): String {
    SIMPLE_DATE_FORMAT.timeZone = TimeZone.getTimeZone(ZoneOffset.UTC)
    return SIMPLE_DATE_FORMAT.format(this)
}
fun Instant.toIsoString(): String {
    return DATE_TIME_FORMATTER.format(this)
}
fun LocalDate.toIsoString(): String {
    return DATE_TIME_FORMATTER.format(this.atTime(0, 0))
}
fun LocalDateTime.toIsoString(): String {
    return DATE_TIME_FORMATTER.format(this)
}
fun ZonedDateTime.toIsoString(): String {
    return DATE_TIME_FORMATTER.format(this)
}

fun main() {
    println(Instant.now().toIsoString())
    println(LocalDateTime.now().toIsoString())
    println(ZonedDateTime.now().toIsoString())
}