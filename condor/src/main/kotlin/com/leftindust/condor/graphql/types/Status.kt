package com.leftindust.condor.graphql.types

data class CondorStatus(
    val isAlive: Boolean,
    val connectedToDatabase: Boolean,
)
