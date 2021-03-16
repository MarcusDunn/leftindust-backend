package com.leftindust.mockingbird.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AuthorizationKtTest {

    @Test
    fun `is allowed on allowed value returns true`() {
        val authorization = Authorization.Allowed
        assertEquals(true, authorization.isAllowed())
    }

    @Test
    fun `is allowed on denied value returns false`() {
        val authorization = Authorization.Denied
        assertEquals(false, authorization.isAllowed())
    }
}