package com.leftindust.mockingbird.auth

import com.leftindust.mockingbird.auth.impl.VerifiedFirebaseToken
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod

internal class ContextFactoryTest {
    private val contextFactory = ContextFactory()

    @Test
    fun generateContext() {
        val actual = runBlocking {
            contextFactory.generateContext(
                request = mockk {
                    every { method } returns HttpMethod.POST
                    every { headers["mediq-auth-token"] } returns listOf("123456")
                }
            )
        }
        assertEquals(actual, GraphQLAuthContext(VerifiedFirebaseToken("123456")))
    }
}