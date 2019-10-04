package assertions.util

import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*

private val DATE_TIME_FORMATTER =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

fun Date.toIsoString(): String {
    DATE_TIME_FORMATTER.timeZone = TimeZone.getTimeZone(ZoneOffset.UTC)
    return DATE_TIME_FORMATTER.format(this)
}