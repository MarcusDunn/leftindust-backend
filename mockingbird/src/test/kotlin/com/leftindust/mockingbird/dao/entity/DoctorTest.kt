package com.leftindust.mockingbird.dao.entity

import biweekly.component.VEvent
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DoctorTest {

    @Test
    fun addPatient() {
        val doctor = EntityStore.doctor()

        val patient = spyk<Patient>().apply {
            doctors = emptySet()
        }

        doctor.addPatient(patient)

        assertEquals(patient, doctor.patients.first().patient)
        assertEquals(doctor, patient.doctors.first().doctor)
    }

    @Test
    fun getEventsBetween() {
        val mockkEvent1 = mockk<VEvent>("event1", relaxed = true)
        val mockkEvent2 = mockk<VEvent>("event2", relaxed = true)

        val doctor = EntityStore.doctor().apply {
            schedule = mockk {
                every { getEventsBetween(any(), any()) } returns listOf(mockkEvent1, mockkEvent2)
            }
        }
        val expected = listOf(Doctor.DocVEvent(doctor, mockkEvent1), Doctor.DocVEvent(doctor, mockkEvent2))
        val result = doctor.getEventsBetween(mockk(), mockk())

        assertEquals(expected, result)

    }
}