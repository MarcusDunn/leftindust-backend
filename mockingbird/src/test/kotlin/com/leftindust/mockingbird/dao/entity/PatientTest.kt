package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.enums.Sex
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PatientTest {

    @Test
    fun addDoctor() {
        val patient = Patient(
            firstName = "marcus",
            lastName = "dunn",
            sex = Sex.Male,
        )

        val doctor = spyk<Doctor>() {
            patients = emptySet()
        }

        patient.addDoctor(doctor)

        assertEquals(doctor, patient.doctors.first().doctor)
        assertEquals(patient, doctor.patients.first().patient)
    }
}