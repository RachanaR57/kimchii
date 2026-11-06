package org.app.project.ui

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * True for a real calendar date written as YYYY-MM-DD.
 * Entries are sorted by this text, so anything else would end up in the wrong order.
 */
internal fun isValidIsoDate(text: String): Boolean {
    val match = Regex("""(\d{4})-(\d{2})-(\d{2})""").matchEntire(text.trim()) ?: return false
    val (year, month, day) = match.destructured.toList().map { it.toInt() }
    if (month !in 1..12) return false
    val isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
    val daysInMonth = when (month) {
        2 -> if (isLeapYear) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    return day in 1..daysInMonth
}

/** The date picker reports the chosen day as UTC-midnight milliseconds; turn that into YYYY-MM-DD. */
@OptIn(ExperimentalTime::class)
internal fun epochMillisToIsoDate(millis: Long): String =
    Instant.fromEpochMilliseconds(millis).toString().take(10)

/** YYYY-MM-DD back to UTC-midnight milliseconds, or null if the text isn't a real date. */
@OptIn(ExperimentalTime::class)
internal fun isoDateToEpochMillis(date: String): Long? {
    if (!isValidIsoDate(date)) return null
    return Instant.parse("${date.trim()}T00:00:00Z").toEpochMilliseconds()
}

private val monthNames = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

/** "2026-11-06" -> "6 Nov 2026". Text that isn't a valid date is returned unchanged. */
internal fun formatDisplayDate(date: String): String {
    if (!isValidIsoDate(date)) return date
    val (year, month, day) = date.trim().split("-")
    return "${day.toInt()} ${monthNames[month.toInt() - 1]} $year"
}
