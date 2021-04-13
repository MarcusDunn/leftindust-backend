package com.leftindust.mockingbird.dao.entity

import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.Instant

internal class ScheduleTest {

    @Test
    fun getEventsBetween() {
        val janSecond = Timestamp.valueOf("2020-01-02 09:01:15")

        val schedule = Schedule(
            events = mutableSetOf(
                mockk(relaxed = true) {
                    every { startTime } returns janSecond
                }
            )
        )

        val janFirst = Timestamp.valueOf("2020-01-01 09:01:15")
        val janThird = Timestamp.valueOf("2020-01-03 09:01:15")

        val actual = schedule.getEventsBetween(janFirst, janThird)
        assertEquals(1, actual.size)
    }

    @Test
    fun `getEventsBetween with recurrence`() {
        val jan1st2019 = Timestamp.valueOf("2019-01-01 09:01:15")

        val schedule = Schedule(
            events = mutableSetOf(
                EntityStore.event("ScheduleTest.getEventsBetween with recurrence").apply {
                    startTime = jan1st2019
                }
            )
        )

        val jan1st2020 = Timestamp.valueOf("2020-01-01 10:01:15")
        val jan1st2021 = Timestamp.valueOf("2021-01-01 05:01:15")

        val actual = schedule.getEventsBetween(jan1st2020, jan1st2021)
        actual.forEach {
            assert(
                it.startTime.toInstant().isAfter(jan1st2020.toInstant())
            ) { "${it.startTime} is not before jan1st2020" }
            assert(it.startTime.time + it.durationMillis!! < jan1st2020.time) {
                "${
                    Timestamp.from(Instant.ofEpochMilli(it.startTime.time + it.durationMillis!!)).toLocalDateTime()
                } is not after jan1st2020"
            }
        }
    }
}