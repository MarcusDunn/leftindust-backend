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
    override val phoneNumbers: List<GraphQLPhoneNumber>,
    private val authContext: GraphQLAuthContext,
) : GraphQLPerson {
    constructor(emergencyContact: EmergencyContact, authContext: GraphQLAuthContext) : this(
        firstName = emergencyContact.firstName,
        middleName = emergencyContact.middleName,
        lastName = emergencyContact.lastName,
        relationship = emergencyContact.relationship,
        authContext = authContext,
        phoneNumbers = mapOf(
            emergencyContact.cellNumber to GraphQLPhoneType.Cell,
            emergencyContact.homeNumber to GraphQLPhoneType.Home,
            emergencyContact.workNumber to GraphQLPhoneType.Work,
        )
            .map { GraphQLPhoneNumber(it.key ?: return@map null, it.value) }
            .filterNotNull()
    )
}

