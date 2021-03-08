package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName
import com.leftindust.mediq.dao.entity.AccessControlList

@GraphQLName("Permissions")
data class GraphQLPermissions(
    val groupPerms: List<GraphQLPermission>,
    val userPerms: List<GraphQLPermission>,
) {
    constructor(perms: List<AccessControlList>) : this(perms.partition { it.group != null })

    private constructor(perms: Pair<List<AccessControlList>, List<AccessControlList>>) : this(
        groupPerms = perms.first.map { GraphQLPermission(it.action) },
        userPerms = perms.second.map { GraphQLPermission(it.action) },
    )
}

