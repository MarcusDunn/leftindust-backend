package com.leftindust.condor

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class ApplicationConfig {
    companion object {
        object FirebaseConfig {
            const val SERVICE_ACCOUNT_KEY_PATH = "src/main/resources/serviceAccountKey.json"
            const val DATABASE_URL = "https://mediq-backend.firebaseio.com"
        }
    }

    @Bean
    fun firebase(): FirebaseApp {
        // dont init twice check
        if (kotlin.runCatching { FirebaseApp.getInstance() }.isSuccess) return FirebaseApp.getInstance()
        return with(FirebaseConfig) {
            val serviceAccount = FileInputStream(SERVICE_ACCOUNT_KEY_PATH)
            val options = com.google.firebase.FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(DATABASE_URL)
                .build()
            FirebaseApp.initializeApp(options)
        }
    }

    @Bean
    fun firebaseAuth(): FirebaseAuth {
        // make sure firebase is initialized
        firebase()
        return FirebaseAuth.getInstance()
    }
}