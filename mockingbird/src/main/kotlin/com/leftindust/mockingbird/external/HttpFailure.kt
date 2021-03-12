package com.leftindust.mockingbird.external

data class HttpFailure(
    val url: String?,
    val responseMessage: String?,
    val code: Int? = null,
)
