package com.leftindust.mockingbird.dao.entity

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
}