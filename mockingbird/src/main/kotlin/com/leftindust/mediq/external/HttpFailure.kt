package com.leftindust.mediq.external

data class HttpFailure(
    val url: String?,
    val responseMessage: String?,
    val code: Int? = null,
)
