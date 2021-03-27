package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.extensions.plus

data class GraphQLEvent(
    val eid: ID,
    val title: String,
    val description: String?,
    val start: GraphQLTime,
    val end: GraphQLTime,
    private val authContext: GraphQLAuthContext,
    private val doctor: Doctor
) {
    constructor(event: Doctor.DocVEvent, authContext: GraphQLAuthContext) : this(
        eid = ID(event.uid.toString()),
        doctor = event.doctor,
        title = event.summary.value,
        description = event.description.value,
        start = GraphQLTime(event.dateStart.value),
        end = GraphQLTime(event.dateStart + event.duration),
        authContext = authContext,
    )

    fun doctor(): GraphQLDoctor =
        GraphQLDoctor(doctor, doctor.id!!, authContext) // safe nn call provided that the event has been persisted
}