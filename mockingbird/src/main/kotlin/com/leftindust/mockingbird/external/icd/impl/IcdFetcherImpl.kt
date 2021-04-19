package com.leftindust.mockingbird.external.icd.impl

import com.leftindust.mockingbird.external.icd.IcdApiClientConfigBean
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.graphql.types.icd.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class IcdFetcherImpl(
    @Autowired private val config: IcdApiClientConfigBean,
) : IcdFetcher {

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                setPrettyPrinting()
            }
        }
        expectSuccess = false
    }

    override suspend fun getLinearizationEntity(
        releaseId: String,
        code: FoundationIcdCode
    ): GraphQLIcdLinearizationEntity {
        TODO()
    }

    override suspend fun linearization(
        linearizationName: String,
        code: FoundationIcdCode
    ): GraphQLIcdMultiVersion {
        return getLinearization(linearizationName, code)
    }

    override suspend fun linearizationSearch(
        releaseId: String,
        linearizationName: String,
        query: String,
        flatResults: Boolean
    ): GraphQLIcdSearchResult {
        val url = "${config.BASE_URL}/release/11/$releaseId/$linearizationName/search?q=$query&flatResult=$flatResults"
        return getUrlWithIcdHeaders(url)
    }

    private suspend fun getLinearization(
        linearizationName: String,
        code: FoundationIcdCode,
    ): GraphQLIcdMultiVersion {
        val url = "${config.BASE_URL}/release/11/$linearizationName/${code.value}"
        return GraphQLIcdMultiVersion(getUrlWithIcdHeaders(url))
    }


    override suspend fun getDetails(
        code: FoundationIcdCode,
    ): GraphQLIcdFoundationEntity {
        val url = "${config.BASE_URL}/entity/${code.value}"
        return GraphQLIcdFoundationEntity(getUrlWithIcdHeaders(url))
    }

    override suspend fun search(
        query: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): GraphQLIcdSearchResult {
        val url = "${config.BASE_URL}/entity/search?q=$query&useFlexisearch=$flexiSearch&flatResults=$flatResults"
        return getUrlWithIcdHeaders(url)
    }

    private suspend inline fun <reified T> getUrlWithIcdHeaders(url: String): T {
        return client.get {
            url(url)
            header("Accept", "application/json")
            header("Accept-Language", "en")
            header("API-Version", "v2")
        }
    }
}