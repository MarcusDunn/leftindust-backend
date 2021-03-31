package integration

import org.springframework.test.web.reactive.server.WebTestClient

fun WebTestClient.ResponseSpec.verifyOnlyDataExists(expectedQuery: String): WebTestClient.BodyContentSpec {
    return try {
        this.expectBody()
            .jsonPath("$DATA_JSON_PATH.$expectedQuery").exists()
            .jsonPath(ERRORS_JSON_PATH).doesNotExist()
            .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
    } catch (e: AssertionError) {
        expectBody().json("") // prints it
    }
}

fun WebTestClient.ResponseSpec.verifyData(
    expectedQuery: String,
    expectedData: String
): WebTestClient.BodyContentSpec {
    return this.expectStatus().isOk
        .verifyOnlyDataExists(expectedQuery)
        .jsonPath("$DATA_JSON_PATH.$expectedQuery").isEqualTo(expectedData)
}

fun WebTestClient.ResponseSpec.verifyError(expectedError: String): WebTestClient.BodyContentSpec {
    return this.expectStatus().isOk
        .expectBody()
        .jsonPath(DATA_JSON_PATH).doesNotExist()
        .jsonPath("$ERRORS_JSON_PATH.[0].message").isEqualTo(expectedError)
        .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()
}
