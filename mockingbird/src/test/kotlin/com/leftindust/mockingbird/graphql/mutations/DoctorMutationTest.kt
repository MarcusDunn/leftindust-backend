package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DoctorMutationTest {

    @Test
    fun addDoctor() {
        val mockkGraphQLAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val mockkDoctor = mockk<Doctor>(relaxed = true) {
            every { id } returns 1000L
        }

        val mockkDoctorDao = mockk<DoctorDao>() {
            coEvery { addDoctor(any(), any()) } returns mockkDoctor
        }

        val mockkGraphQLDoctorInput = mockk<GraphQLDoctorInput>() {
            every { user } returns null
        }

        val doctorMutation = DoctorMutation(mockkDoctorDao, mockk())

        val result = runBlocking { doctorMutation.addDoctor(mockkGraphQLDoctorInput, mockkGraphQLAuthContext) }

        val expected = GraphQLDoctor(mockkDoctor, mockkDoctor.id!!, mockkGraphQLAuthContext)

        assertEquals(expected, result)
    }

    @Test
    fun updateDoctor() {
        val mockkGraphQLAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val mockkDoctor = mockk<Doctor>(relaxed = true) {
            every { id } returns 1000L
        }

        val mockkDoctorDao = mockk<DoctorDao>() {
            coEvery { editDoctor(any(), any()) } returns mockkDoctor
        }

        val mockkGraphQLDoctorInput = mockk<GraphQLDoctorEditInput>()

        val doctorMutation = DoctorMutation(mockkDoctorDao, mockk())

        val result = runBlocking { doctorMutation.updateDoctor(mockkGraphQLDoctorInput, mockkGraphQLAuthContext) }

        val expected = GraphQLDoctor(mockkDoctor, mockkDoctor.id!!, mockkGraphQLAuthContext)

        assertEquals(expected, result)
    }
}