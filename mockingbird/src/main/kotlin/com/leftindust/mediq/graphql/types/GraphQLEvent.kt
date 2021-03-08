package com.leftindust.mediq.graphql.types

import biweekly.property.DateOrDateTimeProperty
import biweekly.property.DurationProperty
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.entity.Doctor
import java.sql.Timestamp

data class GraphQLEvent(
    val eid: ID,
    val title: String,
    val description: String?,
    val start: GraphQLTime,
    val end: GraphQLTime,
    private val authContext: GraphQLAuthContext,
    private val doctor: Doctor
) {
    constructor(event: Doctor.MediqVEvent, authContext: GraphQLAuthContext) : this(
        eid = ID(event.uid.toString()),
        doctor = event.doctor,
        title = event.summary.value,
        description = event.description.value,
        start = GraphQLTime(event.dateStart.value),
        end = GraphQLTime(event.dateStart + event.duration),
        authContext = authContext,
    )

    fun doctor(): GraphQLDoctor = GraphQLDoctor(doctor, authContext)
}

private operator fun DateOrDateTimeProperty.plus(duration: DurationProperty): Timestamp {
    return Timestamp(this.value.toInstant().epochSecond * 1000 + duration.value.toMillis())
}
