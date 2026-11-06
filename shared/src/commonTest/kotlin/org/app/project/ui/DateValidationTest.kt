package org.app.project.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateValidationTest {

    @Test
    fun acceptsRealIsoDates() {
        assertTrue(isValidIsoDate("2026-03-14"))
        assertTrue(isValidIsoDate(" 2026-12-31 "))
        assertTrue(isValidIsoDate("2024-02-29")) // leap year
    }

    @Test
    fun rejectsWrongFormats() {
        assertFalse(isValidIsoDate(""))
        assertFalse(isValidIsoDate("14-03-2026"))
        assertFalse(isValidIsoDate("2026-3-14"))
        assertFalse(isValidIsoDate("2026/03/14"))
        assertFalse(isValidIsoDate("last summer"))
    }

    @Test
    fun rejectsDatesThatDoNotExist() {
        assertFalse(isValidIsoDate("2026-13-01"))
        assertFalse(isValidIsoDate("2026-00-10"))
        assertFalse(isValidIsoDate("2026-04-31"))
        assertFalse(isValidIsoDate("2026-02-29")) // not a leap year
        assertFalse(isValidIsoDate("2026-01-00"))
    }

    @Test
    fun convertsPickerMillisToIsoDate() {
        assertEquals("1970-01-01", epochMillisToIsoDate(0L))
        // 2026-11-06T00:00:00Z, which is what the date picker reports for 6 Nov 2026
        assertEquals("2026-11-06", epochMillisToIsoDate(1_793_923_200_000L))
    }

    @Test
    fun isoDateRoundTripsThroughMillis() {
        for (date in listOf("2026-11-06", "2024-02-29", "1999-12-31", "2030-01-01")) {
            val millis = isoDateToEpochMillis(date)
            assertEquals(date, epochMillisToIsoDate(millis!!))
        }
        assertNull(isoDateToEpochMillis(""))
        assertNull(isoDateToEpochMillis("2026-02-30"))
    }

    @Test
    fun formatsDatesForDisplay() {
        assertEquals("6 Nov 2026", formatDisplayDate("2026-11-06"))
        assertEquals("29 Feb 2024", formatDisplayDate("2024-02-29"))
        assertEquals("not a date", formatDisplayDate("not a date"))
    }
}
