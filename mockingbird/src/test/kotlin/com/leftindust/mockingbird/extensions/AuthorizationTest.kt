package com.leftindust.mockingbird.extensions

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class AuthorizationTest {
    @Test
    internal fun testCreateAllowed() {
        when (returnsAllowed()) {
            Authorization.Allowed -> return
            Authorization.Denied -> fail { "expected to enter Allowed branch, instead fell through to Denied" }
        }
    }

    @Test
    internal fun testCreateDenied() {
        when (returnsDenied()) {
            Authorization.Denied -> return
            Authorization.Allowed -> fail { "expected to enter Denied branch, instead fell through to Allowed" }
        }
    }

    @Test
    internal fun testIsAllowed() {
        assert(returnsAllowed().isAllowed())
        assert(!returnsAllowed().isDenied())
    }

    @Test
    internal fun testIsDenied() {
        assert(returnsDenied().isDenied())
        assert(!returnsDenied().isAllowed())
    }

    private fun returnsAllowed(): Authorization {
        return Authorization.Allowed
    }

    private fun returnsDenied(): Authorization {
        return Authorization.Denied
    }

}