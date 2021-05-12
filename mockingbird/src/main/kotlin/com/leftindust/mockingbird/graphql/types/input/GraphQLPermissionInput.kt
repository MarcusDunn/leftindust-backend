package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.graphql.types.GraphQLUtcTime

data class GraphQLPermissionInput(
    val referencedTableName: Tables,
    val permissionType: Crud,
    val startTime: GraphQLUtcTime? = null,
    val endTime: GraphQLUtcTime? = null,
    val rowId: ID? = null,
    val columnName: String? = null,
)

