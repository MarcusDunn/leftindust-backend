package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.extensions.getOrThrow
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
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
        val patient = EntityStore.patient("PatientTest.addDoctor")

        val doctor = spyk<Doctor>() {
            patients = emptySet()
        }

        patient.addDoctor(doctor)

        assertEquals(doctor, patient.doctors.first().doctor)
        assertEquals(patient, doctor.patients.first().patient)
    }

    @Test
    internal fun `create by GraphQLPatientInput`() {
        val graphQLPatientInput = EntityStore.graphQLPatientInput("PatientTest.create by GraphQLPatientInput")
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

    @Test
    fun setByGqlInput() {
        val patient = EntityStore.patient("PatientTest.setByGqlInput").apply { id = 1 }

        val gqlInput = GraphQLPatientInput(
            pid = OptionalInput.Defined(gqlID(1)),
            firstName = OptionalInput.Defined("new name")
        )

        patient.setByGqlInput(gqlInput, mockk())

        assertEquals(gqlInput.firstName.getOrThrow(), patient.nameInfo.firstName)
    }
}