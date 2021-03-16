package com.leftindust.mockingbird.extensions

/**
 * class denoting the result of a authorization request
 */
enum class Authorization {
    Allowed,
    Denied,
}

fun Authorization.isAllowed(): Boolean {
    return when (this) {
        Authorization.Allowed -> true
        Authorization.Denied -> false
    }
}
