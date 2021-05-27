package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLUser

@GraphQLName("UserInput")
data class GraphQLUserInput(
    val uid: String,
    val nameInfo: GraphQLNameInfoInput,
    val group: GraphQLUser.Group.ID? = null,
)

@GraphQLName("UserEditInput")
@GraphQLDescription("edits the user with the given uid, fields left unset will not be edited ")
data class GraphQLUserEditInput(
    val uid: String,
    val group: OptionalInput<GraphQLUser.Group.ID?> = OptionalInput.Undefined,
)