package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.entity.enums.Relationship

@GraphQLName("EmergencyContact")
data class GraphQLEmergencyContact(
    override val firstName: String,
    override val middleName: String?,
    override val lastName: String,
    val relationship: Relationship,
    override val phones: List<GraphQLPhone>,
    private val authContext: GraphQLAuthContext,
) : GraphQLPerson {
    constructor(emergencyContact: EmergencyContact, authContext: GraphQLAuthContext) : this(
        firstName = emergencyContact.firstName,
        middleName = emergencyContact.middleName,
        lastName = emergencyContact.lastName,
        relationship = emergencyContact.relationship,
        authContext = authContext,
        phones = emergencyContact.phone.map { GraphQLPhone(it) }
    )
}