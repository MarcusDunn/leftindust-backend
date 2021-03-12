package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.RecordType
import com.leftindust.mockingbird.extensions.gqlID

@GraphQLName("Record")
data class GraphQLRecord(
    val rid: ID,
    val creationDate: GraphQLTime,
    val type: RecordType,
    private val patient: Patient,
    private val authContext: GraphQLAuthContext,
) {
    constructor(record: MediqRecord, graphQLAuthContext: GraphQLAuthContext) : this(
        rid = gqlID(record.rid),
        creationDate = GraphQLTime(record.creationDate),
        type = record.type,
        patient = record.patient,
        authContext = graphQLAuthContext,
    )

    fun measurements(): List<Measurement> = TODO()
    fun patient(): GraphQLPatient = GraphQLPatient(patient, patient.id!!, authContext)

    data class Measurement(
        val name: String,
        val magnitude: Float,
        val unit: String,
    )
}
