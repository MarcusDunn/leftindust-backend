package com.leftindust.mediq.auth

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.leftindust.mediq.dao.Tables
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.extensions.isDenied
import com.leftindust.mediq.graphql.queries.IcdQuery
import com.leftindust.mediq.graphql.queries.PatientQuery
import com.leftindust.mediq.helper.FakeAuth
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


    private val invalidAuthToken = FakeAuth.Invalid.Token

    @Test
    internal suspend fun `test auth fails on invalid token`() {
        val readToPatient = Action(Crud.READ to Tables.Patient)
        val result = authorizer.getAuthorization(readToPatient, invalidAuthToken)
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
        val authContext = GraphQLAuthContext(mediqAuthToken = invalidAuthToken)
        assertThrows<GraphQLKotlinException> {
            runBlocking {
                patientQuery.patient(gqlID(1), authContext)
            }
        }
    }
}