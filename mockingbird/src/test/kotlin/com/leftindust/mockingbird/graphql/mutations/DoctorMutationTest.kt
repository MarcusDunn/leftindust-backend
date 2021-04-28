package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DoctorMutationTest {

    @Test
    fun addDoctor() {
        val graphQLAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val doctor = mockk<Doctor>(relaxed = true) {
            every { id } returns 1000L
        }

        val doctorDao = mockk<DoctorDao>() {
            coEvery { addDoctor(any(), any()) } returns doctor
        }

        val graphQLDoctorInput = mockk<GraphQLDoctorInput>() {
            every { user } returns null
        }

        val doctorMutation = DoctorMutation(doctorDao, mockk())

        val result = runBlocking { doctorMutation.addDoctor(graphQLDoctorInput, graphQLAuthContext) }

        val expected = GraphQLDoctor(doctor, doctor.id!!, graphQLAuthContext)

        assertEquals(expected, result)
    }

    @Test
    fun updateDoctor() {

    }
}