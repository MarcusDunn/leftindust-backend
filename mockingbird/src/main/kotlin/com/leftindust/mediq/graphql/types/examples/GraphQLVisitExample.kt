package com.leftindust.mediq.graphql.types.examples

import com.expediagroup.graphql.scalars.ID

data class GraphQLVisitExample(
    val vid: ID,
    val timeBooked: GraphQLTimeExample,
    val timeOfVisit: GraphQLTimeExample,
    val title: String?,
    val description: String?,
    val doctor: GraphQLDoctorExample,
    val patient: GraphQLPatientExample,
)