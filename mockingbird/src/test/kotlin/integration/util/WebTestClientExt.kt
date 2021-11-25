package integration.util

import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import org.springframework.test.web.reactive.server.WebTestClient

fun WebTestClient.gqlRequest(request: String): WebTestClient.ResponseSpec = this.post()
    .uri(GRAPHQL_ENDPOINT)
    .accept(APPLICATION_JSON_MEDIA_TYPE)
    .contentType(GRAPHQL_MEDIA_TYPE)
    .bodyValue(request)
    .exchange()