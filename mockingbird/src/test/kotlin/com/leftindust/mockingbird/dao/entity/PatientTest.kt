package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.hibernate.Session
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PatientTest {

    @Test
    fun addDoctor() {
        val patient = EntityStore.patient()

        val doctor = spyk<Doctor>() {
            patients = emptySet()
        }

        patient.addDoctor(doctor)

        assertEquals(doctor, patient.doctors.first().doctor)
        assertEquals(patient, doctor.patients.first().patient)
    }

    @Test
    internal fun `create by GraphQLPatientInput`() {
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk {
                every { uid } returns "admin"
            }
        }

        val graphQLPatientInput = EntityStore.graphQLPatientInput(authContext)
        val mockkSession = mockk<Session>() {
            every { get(Doctor::class.java, 23L) } returns mockk() {
                every { addPatient(any()) } returns mockk()
            }
            every { get(Doctor::class.java, 55L) } returns mockk() {
                every { addPatient(any()) } returns mockk()
            }
        }

        Patient(graphQLPatientInput, mockkSession)
    }
}