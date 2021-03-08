package com.leftindust.condor

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.inputStream

@Configuration
class ApplicationConfig {
    @OptIn(ExperimentalPathApi::class)
    @Bean
    fun firebaseApplication(): FirebaseApp {
        return FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(Path("condor/src/main/resources/serviceAccountKey.json").inputStream()))
                .build()
        )!!
    }
}
