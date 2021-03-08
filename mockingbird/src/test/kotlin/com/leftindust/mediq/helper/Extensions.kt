import com.leftindust.mediq.extensions.CustomResult
import java.util.*

internal fun <Success, Failure> CustomResult<Success, Failure>.unwrap(): Success {
    return when (this) {
        is com.leftindust.mediq.extensions.Success -> this.value
        is com.leftindust.mediq.extensions.Failure -> throw AssertionError("called unwrap on Failure Result $this")
    }
}

internal fun <Success, Failure> CustomResult<Success, Failure>.unwrapFailure(): Failure {
    return when (this) {
        is com.leftindust.mediq.extensions.Success -> throw AssertionError("unwrapFailure on Success Result $this")
        is com.leftindust.mediq.extensions.Failure -> this.reason
    }
}

internal fun <Success, Failure> CustomResult<Success, Failure>.unwrapFailureOrNull(): Failure? {
    return when (this) {
        is com.leftindust.mediq.extensions.Success -> null
        is com.leftindust.mediq.extensions.Failure -> this.reason
    }
}

