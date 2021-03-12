import com.leftindust.mockingbird.extensions.CustomResult

internal fun <Success, Failure> CustomResult<Success, Failure>.unwrap(): Success {
    return when (this) {
        is com.leftindust.mockingbird.extensions.Success -> this.value
        is com.leftindust.mockingbird.extensions.Failure -> throw AssertionError("called unwrap on Failure Result $this")
    }
}

internal fun <Success, Failure> CustomResult<Success, Failure>.unwrapFailure(): Failure {
    return when (this) {
        is com.leftindust.mockingbird.extensions.Success -> throw AssertionError("unwrapFailure on Success Result $this")
        is com.leftindust.mockingbird.extensions.Failure -> this.reason
    }
}

internal fun <Success, Failure> CustomResult<Success, Failure>.unwrapFailureOrNull(): Failure? {
    return when (this) {
        is com.leftindust.mockingbird.extensions.Success -> null
        is com.leftindust.mockingbird.extensions.Failure -> this.reason
    }
}

