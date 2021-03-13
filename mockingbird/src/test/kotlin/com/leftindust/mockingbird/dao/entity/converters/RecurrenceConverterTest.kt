package com.leftindust.mockingbird.dao.entity.converters

import biweekly.property.RecurrenceRule
import biweekly.util.Frequency
import biweekly.util.Recurrence
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RecurrenceConverterTest {

    @Test
    fun convertToDatabaseColumn() {
        val recurrenceConverter = RecurrenceConverter()

        val daily = RecurrenceRule(Recurrence.Builder(Frequency.DAILY).build())

        val actual = recurrenceConverter.convertToDatabaseColumn(daily)

        fun filterUidAndDTStamp(str: String) =
            str.trim().lines().filterNot { it.startsWith("UID") || it.startsWith("DTSTAMP") }

        val expected = """
            BEGIN:VCALENDAR
            VERSION:2.0
            PRODID:-//Michael Angstadt//biweekly 0.6.6//EN
            BEGIN:VEVENT
            UID:ec5251d7-04db-4c02-8a52-675bf4241d59
            DTSTAMP:20210313T220345Z
            RRULE:FREQ=DAILY
            END:VEVENT
            END:VCALENDAR
        """.trimIndent()

        assertEquals(filterUidAndDTStamp(expected), filterUidAndDTStamp(actual!!))
    }

    @Test
    fun convertToEntityAttribute() {
        val recurrenceConverter = RecurrenceConverter()
        val actual = recurrenceConverter.convertToEntityAttribute(
            """
            BEGIN:VCALENDAR
            VERSION:2.0
            PRODID:-//Michael Angstadt//biweekly 0.6.6//EN
            BEGIN:VEVENT
            UID:ec5251d7-04db-4c02-8a52-675bf4241d59
            DTSTAMP:20210313T220345Z
            RRULE:FREQ=DAILY
            END:VEVENT
            END:VCALENDAR
        """.trimIndent()
        )
        val expected = RecurrenceRule(Recurrence.Builder(Frequency.DAILY).build())

        assertEquals(expected, actual)

    }
}