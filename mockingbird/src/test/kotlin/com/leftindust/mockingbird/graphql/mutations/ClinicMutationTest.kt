package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.ClinicDao
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ClinicMutationTest {
    private val clinicDao = mockk<ClinicDao>()

    @Test
    fun addClinic() {

        val authContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val clinic = mockk<GraphQLClinicInput>()

        val mockkClinic = mockk<Clinic>(relaxed = true) {
            every { id } returns 1000L
        }

        coEvery { clinicDao.addClinic(clinic, authContext.mediqAuthToken) } returns mockkClinic

        val clinicMutation = ClinicMutation(clinicDao)

        val result = runBlocking { clinicMutation.addClinic(clinic, authContext) }

        assertEquals(GraphQLClinic(mockkClinic, mockkClinic.id!!, authContext), result)
    }
}
