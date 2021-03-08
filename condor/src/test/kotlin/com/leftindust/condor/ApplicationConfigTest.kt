package com.leftindust.condor

import com.google.firebase.FirebaseApp
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class ApplicationConfigTest {
    @Test
    internal fun firebaseApp(@Autowired firebaseApp: FirebaseApp) {
        assertDoesNotThrow { firebaseApp.hashCode() }
    }
}