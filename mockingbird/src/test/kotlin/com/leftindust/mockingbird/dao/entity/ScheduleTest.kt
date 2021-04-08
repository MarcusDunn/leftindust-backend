package com.leftindust.mockingbird.dao.entity

import biweekly.property.RecurrenceRule
import biweekly.util.Frequency
import biweekly.util.Recurrence
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Timestamp

internal class ScheduleTest {

    @Test
    fun getEventsBetween() {
        val janSecond = Timestamp.valueOf("2020-01-02 09:01:15")

        val schedule = Schedule(
            events = setOf(
                mockk(relaxed = true) {
                    every { startTime } returns janSecond
                    every { durationMillis } returns 10000
                    every { recurrenceRule } returns null
                }
            )
        )

        val janFirst = Timestamp.valueOf("2020-01-01 09:01:15")
        val febFirst = Timestamp.valueOf("2020-02-01 09:01:15")

        val actual = schedule.getEventsBetween(janFirst, febFirst)
        assertEquals(1, actual.size)
    }

    @Test
    fun `getEventsBetween with recurrence`() {
        val jan1st2019 = Timestamp.valueOf("2019-01-01 09:01:15")

        val schedule = Schedule(
            events = setOf(
                mockk(relaxed = true) {
                    every { startTime } returns jan1st2019
                    every { durationMillis } returns 10000
                    every { recurrenceRule } returns RecurrenceRule(Recurrence.Builder(Frequency.WEEKLY).build())
                }
            )
        )

        val jan1st2020 = Timestamp.valueOf("2020-01-01 10:01:15")
        val jan1st2021 = Timestamp.valueOf("2021-01-01 05:01:15")

        val actual = schedule.getEventsBetween(jan1st2020, jan1st2021)
        println(actual.size)
        actual.forEach {
            assert(it.startTime.toTimestamp().after(jan1st2020)) {"startTime is not after jan first " + it.startTime.toTimestamp().toString()}
            assert(it.endTime.toTimestamp().before(jan1st2021)) {"endtime is not before feb first " + it.endTime.toTimestamp().toString()}
        }
    }
}