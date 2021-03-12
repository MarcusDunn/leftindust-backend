package com.leftindust.mediq.dao.entity

import biweekly.property.RecurrenceRule
import biweekly.util.Frequency
import biweekly.util.Recurrence
import com.leftindust.mediq.dao.entity.enums.Sex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

internal class DoctorTest {

    private val yyyyMMdd: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")!!

    @Test
    fun `getEventsBetween with no recurrence`() {
        val nineteenSeventy = Timestamp.from(Instant.EPOCH)
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Sherman",
            schedule = Schedule(
                scheduleId = 0,
                events = setOf(
                    Event(
                        title = "Eat Breakfast",
                        description = "munch some calories",
                        durationMillis = 100000,
                        startTime = nineteenSeventy,
                        recurrenceRule = null
                    )
                )
            )
        )

        val twentyTwenty = Timestamp.valueOf(
            LocalDate.from(yyyyMMdd.parse("20200101")).atStartOfDay(TimeZone.getTimeZone("UTC").toZoneId())
                .toLocalDateTime()
        )
        val twentyTwentyOne = Timestamp.valueOf(
            LocalDate.from(yyyyMMdd.parse("20210101")).atStartOfDay(TimeZone.getTimeZone("UTC").toZoneId())
                .toLocalDateTime()
        )

        val result = doctor.getEventsBetween(twentyTwenty, twentyTwentyOne)

        assertEquals(0, result.size)
    }

    @Test
    fun `getEventsBetween with recurrence`() {
        val nineteenSeventy = Timestamp.from(Instant.EPOCH)
        val monthly = RecurrenceRule(Recurrence.Builder(Frequency.MONTHLY).build())

        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Sherman",
            schedule = Schedule(
                scheduleId = 0,
                events = setOf(
                    Event(
                        title = "Eat Breakfast",
                        description = "munch some calories",
                        durationMillis = 100000,
                        startTime = nineteenSeventy,
                        recurrenceRule = monthly
                    )
                )
            )
        )

        val twentyTwenty = Timestamp.valueOf(
            LocalDate.from(yyyyMMdd.parse("20200101")).atStartOfDay(TimeZone.getTimeZone("UTC").toZoneId())
                .toLocalDateTime()
        )
        val twentyTwentyOne = Timestamp.valueOf(
            LocalDate.from(yyyyMMdd.parse("20210101")).atStartOfDay(TimeZone.getTimeZone("UTC").toZoneId())
                .toLocalDateTime()
        )

        val result = doctor.getEventsBetween(twentyTwenty, twentyTwentyOne)

        assertEquals(13, result.size)
    }

    @Test
    fun `getEventsBetween without recurrence at exact time`() {
        val twentyTwenty = Timestamp.valueOf(
            LocalDate.from(yyyyMMdd.parse("20200101")).atStartOfDay(TimeZone.getTimeZone("UTC").toZoneId())
                .toLocalDateTime()
        )
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Sherman",
            schedule = Schedule(
                scheduleId = 0,
                events = setOf(
                    Event(
                        title = "Eat Breakfast",
                        description = "munch some calories",
                        durationMillis = 10,
                        startTime = twentyTwenty,
                        recurrenceRule = null
                    )
                )
            )
        )

        val twentyTwentyOne = Timestamp.valueOf(
            LocalDate.from(yyyyMMdd.parse("20210101")).atStartOfDay(TimeZone.getTimeZone("UTC").toZoneId())
                .toLocalDateTime()
        )

        val result = doctor.getEventsBetween(twentyTwenty, twentyTwentyOne)

        assertEquals(1, result.size)
    }

    @Test
    fun addPatient() {
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Sherman",
        )
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )

        val result = doctor.addPatient(patient)

        val expected = doctor.apply {
            patients.toMutableSet().apply {
                add(DoctorPatient(patient, doctor))
            }
        }

        assertEquals(expected, result)
    }
}