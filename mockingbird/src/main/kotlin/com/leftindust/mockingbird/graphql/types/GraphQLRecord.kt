package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.RecordType
import com.leftindust.mockingbird.extensions.gqlID
import java.util.*

@GraphQLName("Record")
data class GraphQLRecord(
    val rid: ID,
    val creationDate: GraphQLUtcTime,
    val type: RecordType,
    private val patient: Patient,
    private val authContext: GraphQLAuthContext,
) {
    @GraphQLName("RecordId")
    data class ID(val id: UUID)

    constructor(record: MediqRecord, graphQLAuthContext: GraphQLAuthContext) : this(
        rid = ID(record.id!!),
        creationDate = GraphQLUtcTime(record.creationDate),
        type = record.type,
        patient = record.patient,
        authContext = graphQLAuthContext,
    )

    fun patient(): GraphQLPatient = GraphQLPatient(patient, authContext)

    data class Measurement(
        val name: String,
        val magnitude: Float,
        val unit: String,
    )
}
