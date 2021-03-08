package com.leftindust.caper.sql

import org.junit.jupiter.api.Test

internal class ConnectionConfigurationTest {

    @Test
    internal fun `test create connection`() {
        ConnectionConfiguration().database()
    }

    @Test
    internal fun `test create connection twice`() {
        ConnectionConfiguration().database()
        ConnectionConfiguration().database()
    }
}