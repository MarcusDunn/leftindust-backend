package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PatientMutationTest {
    private val authContext = mockk<GraphQLAuthContext>()
    private val patientDao = mockk<PatientDao>()


    @Test
    fun addDoctorToPatient() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000
            every { homePhone } returns null
            every { cellPhone } returns null
            every { workPhone } returns null
        }

        coEvery { patientDao.addDoctorToPatient(any(), any(), any()) } returns mockk() {
            every { getOrThrow() } returns mockkPatient
        }

        every { authContext.mediqAuthToken } returns mockk()

        val patientMutation = PatientMutation(patientDao)

        val result = runBlocking { patientMutation.addDoctorToPatient(gqlID(1000), gqlID(2000), authContext) }

        val graphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)

        assertEquals(graphQLPatient, result)
    }

    @Test
    fun updatePatient() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000
            every { homePhone } returns null
            every { cellPhone } returns null
            every { workPhone } returns null
        }

        every { authContext.mediqAuthToken } returns mockk()

        val mockkGraphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)

        coEvery { patientDao.update(any(), any()) } returns mockk {
            every { getOrThrow() } returns mockkPatient
        }

        val patientMutation = PatientMutation(patientDao)

        val mockkGqlPatientInput = mockk<GraphQLPatientInput>()

        val result = runBlocking { patientMutation.updatePatient(mockkGqlPatientInput, authContext) }

        assertEquals(mockkGraphQLPatient, result)
    }

    @Test
    fun addPatient() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000
            every { homePhone } returns null
            every { cellPhone } returns null
            every { workPhone } returns null
        }

        every { authContext.mediqAuthToken } returns mockk()

        val mockkGraphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)


        coEvery { patientDao.addNewPatient(any(), any()) } returns mockk {
            every { getOrThrow() } returns mockkPatient
        }

        val patientMutation = PatientMutation(patientDao)

        val mockkGqlPatientInput = mockk<GraphQLPatientInput>()

        val result = runBlocking { patientMutation.addPatient(mockkGqlPatientInput, authContext) }

        assertEquals(mockkGraphQLPatient, result)
    }
}