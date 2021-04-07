package com.leftindust.mockingbird.external.icd.impl

import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.external.HttpFailure
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
    ): CustomResult<GraphQLIcdLinearizationEntity, HttpFailure> {
        TODO()
    }

    override suspend fun linearization(
        linearizationName: String,
        code: FoundationIcdCode
    ): CustomResult<GraphQLIcdMultiVersion, HttpFailure> {
        return getLinearization(linearizationName, code)
    }

    override suspend fun linearizationSearch(
        releaseId: String,
        linearizationName: String,
        query: String,
        flatResults: Boolean
    ): CustomResult<GraphQLIcdSearchResult, HttpFailure> {
        val url =
            "${config.BASE_URL}/release/11/$releaseId/$linearizationName/search?q=$query&flatResult=$flatResults"
        return kotlin.runCatching {
            Success(getUrlWithIcdHeaders<GraphQLIcdSearchResult>(url))
        }.getOrElse {
            Failure(
                reason = HttpFailure(
                    url = url,
                    responseMessage = "${it.message}"
                )
            )
        }
    }

    private suspend fun getLinearization(
        linearizationName: String,
        code: FoundationIcdCode,
    ): CustomResult<GraphQLIcdMultiVersion, HttpFailure> {
        val url = "${config.BASE_URL}/release/11/$linearizationName/${code.value}"
        return kotlin.runCatching {
            Success(
                GraphQLIcdMultiVersion(
                    getUrlWithIcdHeaders<IcdMultiVersion>(url)
                )
            )
        }.getOrElse {
            Failure(
                reason = HttpFailure(
                    url = url,
                    responseMessage = it.message
                )
            )
        }
    }

    override suspend fun getDetails(
        code: FoundationIcdCode,
    ): CustomResult<GraphQLIcdFoundationEntity, HttpFailure> {
        val url = "${config.BASE_URL}/entity/${code.value}"
        return kotlin.runCatching {
            Success(
                value = GraphQLIcdFoundationEntity(
                    foundationEntity = getUrlWithIcdHeaders(url)
                )
            )
        }.getOrElse {
            Failure(
                reason = HttpFailure(
                    url = url,
                    responseMessage = it.message
                )
            )
        }
    }

    override suspend fun search(
        query: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): CustomResult<GraphQLIcdSearchResult, HttpFailure> {
        val url = "${config.BASE_URL}/entity/search?q=$query&useFlexisearch=$flexiSearch&flatResults=$flatResults"
        val response = getUrlWithIcdHeaders<GraphQLIcdSearchResult>(url)
        return if (response.error) {
            Failure(
                HttpFailure(
                    url = url,
                    responseMessage = response.errorMessage,
                )
            )
        } else {
            Success(response)
        }
    }

    private suspend inline fun <reified T> getUrlWithIcdHeaders(url: String): T {
        val start = System.nanoTime()
        return client.get<T> {
            url(url)
            header("Accept", "application/json")
            header("Accept-Language", "en")
            header("API-Version", "v2")
        }.also { println("networkTime: ${(System.nanoTime() - start) / 1_000_000F} ms") }
    }
}