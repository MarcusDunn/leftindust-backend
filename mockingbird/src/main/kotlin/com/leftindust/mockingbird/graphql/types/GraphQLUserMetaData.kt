package com.leftindust.mockingbird.graphql.types

data class GraphQLUserMetaData(
    val creationTimestamp: Long? = null,
    val lastSignInTimestamp: Long? = null,
    val lastRefreshTimestamp: Long? = null,
)
