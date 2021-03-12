package com.leftindust.mockingbird.graphql

import com.leftindust.mockingbird.auth.ContextFactory
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.http.server.reactive.MockServerHttpResponse

@SpringBootTest
class GenerateContextTest {

    @Test
    internal fun `test GenerateContext fails on invalid token`() {
        val req = MockServerHttpRequest
            .get("https://127.0.0.1:8080/graphql")
            .header(
                "mediq-auth-token",
                "TEST invalid token TEST"
            )
            .build()

        val resp = MockServerHttpResponse()

        runBlocking {
            assert(ContextFactory().generateContext(req, resp).mediqAuthToken.uid == null)
        }

    }
}