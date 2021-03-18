package com.leftindust.mockingbird.graphql.types.examples

import com.expediagroup.graphql.scalars.ID

data class GraphQLVisitExample(
    val vid: ID? = null,
    val timeBooked: GraphQLTimeExample? = null,
    val timeOfVisit: GraphQLTimeExample? = null,
    val title: String? = null,
    val description: String? = null,
    val doctor: GraphQLDoctorExample? = null,
    val patient: GraphQLPatientExample? = null,
    val icdFoundationCode: StringFilter? = null,
)