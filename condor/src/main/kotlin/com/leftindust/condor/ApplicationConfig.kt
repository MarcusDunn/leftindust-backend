package com.leftindust.condor

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class ApplicationConfig {
    companion object {
        const val CREDENTIALS_PATH = "src/main/resources/serviceAccountKey.json"
    }

    @Bean
    fun firebaseApplication(): FirebaseApp {
        return FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(File(CREDENTIALS_PATH).inputStream()))
                .build()
        )!!
    }
}
