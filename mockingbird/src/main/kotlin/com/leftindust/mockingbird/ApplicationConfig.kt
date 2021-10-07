package com.leftindust.mockingbird

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.leftindust.mockingbird.external.icd.IcdApiClientConfigBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.web.cors.reactive.CorsUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.io.*
import java.util.*
import javax.sql.DataSource


@Configuration
class ApplicationConfig {
    private companion object {
        object FireBaseConfig {
            const val SERVICE_ACCOUNT_KEY_PATH = "src/main/resources/mediq-backend-firebase-adminsdk.json"
            const val DATABASE_URL = "https://mediq-backend.firebaseio.com"
        }

        object CorsConfig {
            const val ALLOWED_HEADERS =
                "Mediq-Auth-Token, Content-Type, Access-Control-Allow-Origin, Referer, User-Agent"
            const val ALLOWED_METHODS = "POST, OPTIONS"
            const val ALLOWED_ORIGIN = "*"
            const val MAX_AGE = "3600"
        }

        object HibernateConfig {
            const val ENTITY_PACKAGE = "com.leftindust.mockingbird.dao.entity"
            const val HBM2DDL_AUTO = "none"
            const val DIALECT = "org.hibernate.dialect.PostgreSQLDialect"
        }
    }

    @Bean
    fun firebaseInit(): FirebaseApp {
        if (kotlin.runCatching {
                FirebaseApp.getInstance()
            }.isSuccess) return FirebaseApp.getInstance()
        return try {
            with(FireBaseConfig) {
                val serviceAccount = FileInputStream(SERVICE_ACCOUNT_KEY_PATH)
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
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

    @Bean
    fun firebaseAuth(): FirebaseAuth {
        firebaseInit()
        return FirebaseAuth.getInstance()
    }

    @Bean
    fun corsFilter() = WebFilter { ctx: ServerWebExchange, chain: WebFilterChain ->
        val request = ctx.request
        if (CorsUtils.isCorsRequest(request)) {
            val response = ctx.response
            with(CorsConfig) {
                response.headers.apply {
                    add("Access-Control-Allow-Origin", ALLOWED_ORIGIN)
                    add("Access-Control-Allow-Methods", ALLOWED_METHODS)
                    add("Access-Control-Max-Age", MAX_AGE)
                    add("Access-Control-Allow-Headers", ALLOWED_HEADERS)
                }
            }
            if (request.method == HttpMethod.OPTIONS) {
                ctx.response.statusCode = HttpStatus.OK
                return@WebFilter Mono.empty<Void>()
            }
        }
        return@WebFilter chain.filter(ctx)
    }

    @Bean
    fun icdApiClientConfigBean(): IcdApiClientConfigBean {
        return IcdApiClientConfigBean(
            BASE_URL = System.getenv("WHO_ICD_API") ?: "http://localhost:80/icd",
        )
    }

    @Bean(name = ["entityManagerFactory"])
    fun sessionFactory(dataSource: DataSource): LocalSessionFactoryBean {
        return with(HibernateConfig) {
            LocalSessionFactoryBean().apply {
                setDataSource(dataSource)
                setPackagesToScan(ENTITY_PACKAGE)
                hibernateProperties = Properties().apply {
                    setProperty("hibernate.hbm2ddl.auto", HBM2DDL_AUTO)
                    setProperty("hibernate.dialect", DIALECT)
                }
            }
        }
    }
}

