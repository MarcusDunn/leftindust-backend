package com.leftindust.mockingbird.startup

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LoggingTest {
    val logger: Logger = LogManager.getLogger(this.javaClass)

    @Test
    internal fun `test logger configured`() {
        assert(logger.isErrorEnabled) {"Error should be enabled"}
    }
}