package com.leftindust.mockingbird.extensions

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import org.apache.logging.log4j.LogManager

/**
 * basis for all expected and recoverable errors throughout the application, essentially an enumeration over failure and
 * success.
 * @param S the type to be returned upon success
 * @param F the type to be returned upon failure
 * @property S inner type holding the value of the Success
 * @property S inner type holding the reason for the failure, generally a String
 */
sealed class CustomResult<out S, out F> {
    fun isSuccess() = this is Success
    fun isFailure() = this is Failure

    fun getOrThrow(onFailure: (failure: F) -> Throwable = { CustomResultException("called getOrThrow on failure $it") }): S {
        return when (this) {
            is Success -> this.value
            is Failure -> throw onFailure(this.reason)
                .also { LogManager.getLogger().error("failed to unwrap success on $this") }
        }
    }

    fun getOrNull(): S? {
        return when (this) {
            is Failure -> null
            is Success -> this.value
        }
    }
}

// these are outside of the CustomResult class so we can directly use `is Success` instead of `is CustomResult.Success`
data class Success<out Success>(val value: Success) : CustomResult<Success, Nothing>()
data class Failure<out Failure>(val reason: Failure) : CustomResult<Nothing, Failure>()

class CustomResultException(override val message: String? = null, override val cause: Throwable? = null) :
    GraphQLKotlinException()