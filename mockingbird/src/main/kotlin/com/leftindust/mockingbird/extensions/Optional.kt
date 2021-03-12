package com.leftindust.mockingbird.extensions

import java.util.*

fun <T> Optional<T>.unwrapOrNull(): T? {
    return if (this.isPresent) {
        this.get()
    } else {
        null
    }
}

fun <T> Optional<T>.unwrapAsSuccessOrNull(): Success<T>? {
    return if (this.isPresent) {
        Success(this.get()!!)
    } else {
        null
    }
}