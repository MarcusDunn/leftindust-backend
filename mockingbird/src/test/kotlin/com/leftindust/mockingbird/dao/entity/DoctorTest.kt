package com.leftindust.mockingbird.dao.entity

import integration.util.EntityStore
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DoctorTest {

    @Test
    fun addPatient() {
        val doctor = EntityStore.doctor("DoctorTest.addPatient")

        val patient = spyk<Patient>().apply {
            doctors = emptySet()
        }

        doctor.addPatient(patient)

        assertEquals(patient, doctor.patients.first().patient)
        assertEquals(doctor, patient.doctors.first().doctor)
    }
}