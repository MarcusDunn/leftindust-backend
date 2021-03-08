package com.leftindust.mediq.graphql.types

data class GraphQLUserMetaData(
    val creationTimestamp: Long? = null,
    val lastSignInTimestamp: Long? = null,
    val lastRefreshTimestamp: Long? = null,
)
