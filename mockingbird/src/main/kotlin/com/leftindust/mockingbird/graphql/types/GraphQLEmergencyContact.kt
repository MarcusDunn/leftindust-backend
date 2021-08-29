package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
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
    override val emails: List<GraphQLEmail>,
    private val authContext: GraphQLAuthContext,
    @GraphQLDescription(GraphQLPerson.thumbnailDescription)
    override val thumbnail: String? = null,
) : GraphQLPerson {
    constructor(emergencyContact: EmergencyContact, authContext: GraphQLAuthContext) : this(
        firstName = emergencyContact.nameInfo.firstName,
        middleName = emergencyContact.nameInfo.middleName,
        lastName = emergencyContact.nameInfo.lastName,
        relationship = emergencyContact.relationship,
        authContext = authContext,
        emails = emergencyContact.email.map { GraphQLEmail(it) },
        phones = emergencyContact.phone.map { GraphQLPhone(it) }
    )
}