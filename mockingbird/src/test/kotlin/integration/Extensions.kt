package integration

import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

fun WebTestClient.ResponseSpec.verifyOnlyDataExists(expectedQuery: String): WebTestClient.BodyContentSpec {
    return try {
        this.expectBody()
            .jsonPath("$DATA_JSON_PATH.$expectedQuery").exists()
            .jsonPath(ERRORS_JSON_PATH).doesNotExist()
            .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
    } catch (e: AssertionError) {
        println(this.returnResult<String>())
        throw e
    }
}

fun WebTestClient.ResponseSpec.debugPrint() : WebTestClient.ResponseSpec {
    println(this.returnResult<String>())
    return this
}

fun WebTestClient.ResponseSpec.verifyData(
    expectedQuery: String,
    expectedData: String
): WebTestClient.BodyContentSpec {
    return try {
        this.expectStatus().isOk
            .verifyOnlyDataExists(expectedQuery)
            .jsonPath("$DATA_JSON_PATH.$expectedQuery").isEqualTo(expectedData)
    } catch (e: AssertionError) {
        println(this.returnResult<String>())
        throw e
    }
}

fun WebTestClient.ResponseSpec.verifyError(expectedError: String): WebTestClient.BodyContentSpec {
    return try {
        this.expectStatus().isOk
            .expectBody()
            .jsonPath(DATA_JSON_PATH).doesNotExist()
            .jsonPath("$ERRORS_JSON_PATH.[0].message").isEqualTo(expectedError)
            .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
    } catch (e: AssertionError) {
        println(this.returnResult<String>())
        throw e
    }
}