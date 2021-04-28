package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput

@GraphQLName("DoctorInput")
data class GraphQLDoctorInput(
    val did: ID? = null,
    val user: GraphQLUserInput? = null,
    val nameInfo: GraphQLNameInfoInput,
    val phones: List<GraphQLPhoneInput>? = emptyList(),
    val title: String? = null,
    val dateOfBirth: GraphQLTimeInput,
    val addresses: List<GraphQLAddressInput>? = emptyList(),
    val emails: List<GraphQLEmailInput>? = emptyList(),
    val patients: List<ID>? = emptyList(),
) {
    init {
        if (user?.nameInfo == nameInfo || user == null) {
            // ok
        } else {
            throw IllegalArgumentException("nameInfo for the user and the doctor must be the same")
        }
    }
}