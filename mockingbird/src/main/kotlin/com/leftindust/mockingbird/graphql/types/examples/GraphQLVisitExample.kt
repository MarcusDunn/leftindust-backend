package com.leftindust.mockingbird.graphql.types.examples

import com.expediagroup.graphql.execution.OptionalInput

data class GraphQLVisitExample(
    val vid: OptionalInput<StringFilter> = OptionalInput.Undefined,
    val timeBooked: OptionalInput<GraphQLTimeExample> = OptionalInput.Undefined,
    val timeOfVisit: OptionalInput<GraphQLTimeExample> = OptionalInput.Undefined,
    val title: OptionalInput<StringFilter> = OptionalInput.Undefined,
    val description: OptionalInput<StringFilter> = OptionalInput.Undefined,
    val doctor: OptionalInput<GraphQLDoctorExample> = OptionalInput.Undefined,
    val patient: OptionalInput<GraphQLPatientExample> = OptionalInput.Undefined,
    val icdFoundationCode: OptionalInput<StringFilter> = OptionalInput.Undefined,
)