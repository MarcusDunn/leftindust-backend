package com.leftindust.condor

import com.google.firebase.FirebaseApp
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import java.io.FileNotFoundException

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
        if (kotlin.runCatching { FirebaseApp.getInstance() }.isSuccess) return FirebaseApp.getInstance()
        return try {
            with(FirebaseConfig) {
                val serviceAccount = FileInputStream(SERVICE_ACCOUNT_KEY_PATH)
                val options = com.google.firebase.FirebaseOptions.builder()
                    .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build()
                FirebaseApp.initializeApp(options)
            }
        } catch (e: FileNotFoundException) {
            throw FileNotFoundException(
                "you are missing serviceAccountKey.json for firebase authentication, this is not" +
                        "available in the public repository, you must make your own. See " +
                        "https://firebase.google.com/docs/admin/setup for details."
            )
        }
    }
}