package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.entity.MediqRecord
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.enums.RecordType
import com.leftindust.mediq.extensions.gqlID

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
