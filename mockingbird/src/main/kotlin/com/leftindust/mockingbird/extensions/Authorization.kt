package com.leftindust.mockingbird.extensions

/**
 * class denoting the result of a authorization request
 */
enum class Authorization {
    Allowed,
    Denied,
}

/**
 * convenience function for associating Authorization to boolean
 */
fun Authorization.isAllowed(): Boolean {
    return when (this) {
        Authorization.Allowed -> true
        Authorization.Denied -> false
    }
}

/**
 * convenience function for associating Authorization to boolean
 */
fun Authorization.isDenied() = !this.isAllowed()
