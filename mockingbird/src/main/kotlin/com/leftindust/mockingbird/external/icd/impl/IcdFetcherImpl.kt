package com.leftindust.mockingbird.external.icd.impl

import com.google.gson.JsonObject
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.external.HttpFailure
import com.leftindust.mockingbird.external.icd.IcdApiClientConfigBean
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.graphql.types.icd.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class IcdFetcherImpl(
    @Autowired private val config: IcdApiClientConfigBean,
) : IcdFetcher {
    companion object {
        const val TOKEN_ENDPOINT = "https://icdaccessmanagement.who.int/connect/token"
        const val SCOPE = "icdapi_access"
        const val GRANT_TYPE = "client_credentials"
    }

    var currentToken = IcdAuthToken("none", 0)

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                setPrettyPrinting()
            }
        }
        expectSuccess = false
    }

    init {
        GlobalScope.launch(Dispatchers.IO) {
            withTimeout(2000) {
                getToken().getOrThrow()
            }
        }
    }

    override suspend fun search(
        query: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): CustomResult<GraphQLIcdSearchResult, HttpFailure> {
        return getTokenAndBubbleUpError { token ->
            search(query, token, flexiSearch, flatResults)
        }
    }

    override suspend fun getDetails(code: FoundationIcdCode): CustomResult<GraphQLIcdFoundationEntity, HttpFailure> {
        return getTokenAndBubbleUpError { token ->
            getDetails(code, token)
        }
    }

    override suspend fun getLinearizationEntity(
        releaseId: String,
        code: FoundationIcdCode
    ): CustomResult<GraphQLIcdLinearizationEntity, HttpFailure> {
        return getTokenAndBubbleUpError { token ->
            getLinearizationEntity(releaseId, code = code, token = token)
        }
    }

    override suspend fun linearization(
        linearizationName: String,
        code: FoundationIcdCode
    ): CustomResult<GraphQLIcdMultiVersion, HttpFailure> {
        return getTokenAndBubbleUpError { token ->
            getLinearization(linearizationName, code, token)
        }
    }

    override suspend fun linearizationSearch(
        releaseId: String,
        linearizationName: String,
        query: String,
        flatResults: Boolean
    ): CustomResult<GraphQLIcdSearchResult, HttpFailure> {
        return getTokenAndBubbleUpError { token ->
            getLinearizationSearch(releaseId, linearizationName, query, flatResults, token)
        }
    }

    private suspend fun getLinearizationSearch(
        releaseId: String,
        linearizationName: String,
        query: String,
        flatResults: Boolean,
        token: String
    ): CustomResult<GraphQLIcdSearchResult, HttpFailure> {
        val url =
            "https://id.who.int/icd/release/11/$releaseId/$linearizationName/search?q=$query&flatResult=$flatResults"
        return kotlin.runCatching {
            Success(
                getUrlWithIcdHeaders<GraphQLIcdSearchResult>(url, token)
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

    private suspend fun getLinearization(
        linearizationName: String,
        code: FoundationIcdCode,
        token: String
    ): CustomResult<GraphQLIcdMultiVersion, HttpFailure> {
        val url = "https://id.who.int/icd/release/11/$linearizationName/${code.value}"
        return kotlin.runCatching {
            Success(
                GraphQLIcdMultiVersion(
                    getUrlWithIcdHeaders<IcdMultiVersion>(url, token)
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

    private suspend fun getLinearizationEntity(
        releaseId: String,
        linearizationName: String = "mms",
        code: FoundationIcdCode,
        token: String
    ): CustomResult<GraphQLIcdLinearizationEntity, HttpFailure> {
        val url = "https://id.who.int/icd/release/11/$releaseId/$linearizationName/${code.value}"
        return kotlin.runCatching {
            Success(getUrlWithIcdHeaders<GraphQLIcdLinearizationEntity>(url, token))
        }.getOrElse {
            Failure(
                reason = HttpFailure(
                    url = url,
                    responseMessage = it.message
                )
            )
        }
    }

    private suspend fun getDetails(
        foundationIcdCode: FoundationIcdCode,
        token: String
    ): CustomResult<GraphQLIcdFoundationEntity, HttpFailure> {
        val url = "https://id.who.int/icd/entity/${foundationIcdCode.value}"
        return kotlin.runCatching {
            Success(
                value = GraphQLIcdFoundationEntity(
                    foundationEntity = getUrlWithIcdHeaders(url, token)
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

    private suspend fun search(
        query: String,
        token: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): CustomResult<GraphQLIcdSearchResult, HttpFailure> {
        val url = "https://id.who.int/icd/entity/search?q=$query&useFlexisearch=$flexiSearch&flatResults=$flatResults"
        val response = getUrlWithIcdHeaders<GraphQLIcdSearchResult>(url, token)
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

    private suspend fun getToken(): CustomResult<String, HttpFailure> {
        if (currentToken.expiresAtMillis > System.currentTimeMillis()) return Success(currentToken.access_token)
        val json = client.post<JsonObject>(TOKEN_ENDPOINT) {
            header("API-Version", "v2")
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.FormUrlEncoded)
            body = "client_id=${config.CLIENT_ID}" +
                    "&client_secret=${config.CLIENT_SECRET}" +
                    "&scope=${SCOPE}" +
                    "&grant_type=${GRANT_TYPE}"
        }
        currentToken = IcdAuthToken(
            access_token = json["access_token"].asString,
            expiresAtMillis = System.currentTimeMillis() + (json["expires_in"].asInt * 1000) - 1000 // minus 1000 here to give leeway on getting a new token so we dont make a request that fails because token expired in transit
        )
        return Success(json["access_token"].asString)
    }

    private suspend inline fun <reified T> getUrlWithIcdHeaders(url: String, token: String): T {
        return client.get {
            url(url)
            header("Authorization", "Bearer $token")
            header("Accept", "application/json")
            header("Accept-Language", "en")
            header("API-Version", "v2")
        }
    }

    private suspend fun <T> getTokenAndBubbleUpError(handleSuccess: suspend (String) -> CustomResult<T, HttpFailure>): CustomResult<T, HttpFailure> {
        return when (val token = getToken()) {
            is Success -> try {
                handleSuccess(token.value)
            } catch (e: ServerResponseException) {
                Failure(
                    HttpFailure(
                        url = null,
                        responseMessage = e.toString(),
                    )
                )
            }
            is Failure -> Failure(
                HttpFailure(
                    token.reason.url,
                    responseMessage = "not authorized to access WHO ICD API, this is likely a server configuration issue",
                )
            )
        }
    }

    data class IcdAuthToken(val access_token: String, val expiresAtMillis: Long)
}

