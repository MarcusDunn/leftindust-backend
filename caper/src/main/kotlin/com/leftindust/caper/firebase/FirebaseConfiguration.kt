package com.leftindust.caper.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class FirebaseConfiguration {
    private final val serviceAccountKeyPath = "caper/src/main/resources/serviceAccountKey.json"

    @Bean
    fun firebase(): FirebaseApp {
        val instance = try {
            FirebaseApp.getInstance()
        } catch (wasAlreadyInitialized: IllegalStateException) {
            null
        }
        return if (instance == null) {
            val credentials = GoogleCredentials.fromStream(File(serviceAccountKeyPath).inputStream())
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
            FirebaseApp.initializeApp(options)
        } else {
            instance
        }
    }
}
