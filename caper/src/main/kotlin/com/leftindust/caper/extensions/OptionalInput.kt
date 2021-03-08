package com.leftindust.caper.extensions

import com.expediagroup.graphql.generator.execution.OptionalInput

fun <T> OptionalInput<T>.getOrDefault(default: T): T {
    return when (this) {
        is OptionalInput.Undefined -> default
        is OptionalInput.Defined -> this.value ?: default
    }
}