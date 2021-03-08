package com.leftindust.caper.firebase

import org.junit.jupiter.api.Test

internal class FirebaseConfigurationTest {
    private val firebaseConfiguration = FirebaseConfiguration()

    @Test
    internal fun `init firebase app`() {
        firebaseConfiguration.firebase()
    }

    @Test
    internal fun `init firebase twice`() {
        firebaseConfiguration.firebase()
        firebaseConfiguration.firebase()
    }
}