package com.leftindust.mockingbird.util.integration

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
@Testcontainers
@ContextConfiguration(initializers = [IntegrationTest.Initialize::class])
@Tag("Integration")
abstract class IntegrationTest {

    companion object {
        val postgres = PostgreSQLContainer(PostgreSQLContainer.IMAGE).apply {
            waitingFor(LogMessageWaitStrategy().withRegEx(".*database system is ready to accept connections.*"))
            start()
        }
    }

    object Initialize : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(it: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                it,
                "spring.datasource.url=" + postgres.jdbcUrl,
                "spring.datasource.username=" + postgres.username,
                "spring.datasource.password=" + postgres.password
            )
        }
    }
}

