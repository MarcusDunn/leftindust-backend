package com.leftindust.mockingbird.external.icd.impl

import com.leftindust.mockingbird.external.icd.IcdApiClient
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.graphql.types.icd.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLReleaseIdInput
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class IcdFetcherImpl(
    @Autowired private val config: IcdApiClient,
) : IcdFetcher {

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                setPrettyPrinting()
            }
        }
        expectSuccess = true
    }

    override suspend fun linearization(
        linearizationName: String,
        code: GraphQLFoundationIcdCode
    ): GraphQLIcdMultiVersion {
        val url = "${config.url}/release/11/$linearizationName/${code.code}"
        return GraphQLIcdMultiVersion(getUrlWithIcdHeaders(url))
    }

    override suspend fun linearizationSearch(
        query: String,
        linearizationName: String,
        flatResults: Boolean,
        flexiSearch: Boolean,
    ): GraphQLIcdSearchResult {
        val url = "${config.url}/release/11/${GraphQLReleaseIdInput.CURRENT}/$linearizationName/search?q=$query&flatResult=$flatResults&useFlexisearch=$flexiSearch"
        return getUrlWithIcdHeaders(url)
    }

    override suspend fun getDetails(
        code: GraphQLFoundationIcdCode,
    ): GraphQLIcdFoundationEntity {
        val url = "${config.url}/entity/${code.code}"
        return GraphQLIcdFoundationEntity(getUrlWithIcdHeaders(url))
    }

    override suspend fun search(
        query: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): GraphQLIcdSearchResult {
        val url = "${config.url}/entity/search?q=$query&useFlexisearch=$flexiSearch&flatResults=$flatResults"
        return getUrlWithIcdHeaders(url)
    }

    override suspend fun linearizationEntity(
        code: GraphQLFoundationIcdCode
    ): GraphQLIcdLinearizationEntity {
        val url = "${config.url}/release/11/${GraphQLReleaseIdInput.CURRENT}/mms/${code.code}"
        return GraphQLIcdLinearizationEntity(getUrlWithIcdHeaders(url))
    }

    private suspend inline fun <reified T> getUrlWithIcdHeaders(url: String): T {
        println(url)
        return client.get {
            url(url)
            header("Accept", "application/json")
            header("Accept-Language", "en")
            header("API-Version", "v2")
        }
    }
}