package com.leftindust.mockingbird.auth

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.extensions.isDenied
import com.leftindust.mockingbird.graphql.queries.IcdQuery
import com.leftindust.mockingbird.graphql.queries.PatientQuery
import com.leftindust.mockingbird.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestAuthorization(
    @Autowired private var authorizer: Authorizer,
    @Autowired private var icdQuery: IcdQuery,
    @Autowired private var patientQuery: PatientQuery,
) {

    @Test
    internal suspend fun `test auth fails on invalid token`() {
        val readToPatient = Action(Crud.READ to Tables.Patient)
        val result = authorizer.getAuthorization(readToPatient, FakeAuth.Invalid.Token)
        assert(result.isDenied())
    }

    @Test
    internal fun `test request rejected for IcdCode`() {
        assertThrows<GraphQLKotlinException> {
            runBlocking {
                icdQuery.searchIcd("Sleep", authContext = FakeAuth.Invalid.Context)
            }
        }
    }

    @Test
    internal fun `test request rejected for getPatient`() {
        val authContext = GraphQLAuthContext(mediqAuthToken = FakeAuth.Invalid.Token)
        assertThrows<GraphQLKotlinException> {
            runBlocking {
                patientQuery.patient(gqlID(1), authContext)
            }
        }
    }
}