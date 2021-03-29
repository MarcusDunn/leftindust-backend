package com.leftindust.condor.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.google.firebase.auth.UserRecord

@GraphQLName("User")
data class GraphQLUser(
    val uid: String
) {
    constructor(userRecord: UserRecord) : this(userRecord.uid)
}
