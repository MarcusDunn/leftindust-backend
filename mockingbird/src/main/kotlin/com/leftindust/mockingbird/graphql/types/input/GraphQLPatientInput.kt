package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.execution.OptionalInput
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.graphql.types.GraphQLPhoneNumber
import com.leftindust.mockingbird.graphql.types.GraphQLTime

@GraphQLName("PatientInput")
data class GraphQLPatientInput(
    val pid: OptionalInput<ID> = OptionalInput.Undefined,
    val firstName: OptionalInput<String> = OptionalInput.Undefined,
    val middleName: OptionalInput<String?> = OptionalInput.Undefined,
    val lastName: OptionalInput<String> = OptionalInput.Undefined,
    val phoneNumbers: OptionalInput<List<GraphQLPhoneNumber>> = OptionalInput.Undefined,
    val dateOfBirth: OptionalInput<GraphQLTime> = OptionalInput.Undefined,
    val address: OptionalInput<String> = OptionalInput.Undefined,
    val email: OptionalInput<String> = OptionalInput.Undefined,
    val insuranceNumber: OptionalInput<ID> = OptionalInput.Undefined,
    val sex: OptionalInput<Sex> = OptionalInput.Undefined,
    val gender: OptionalInput<String> = OptionalInput.Undefined,
    val ethnicity: OptionalInput<Ethnicity> = OptionalInput.Undefined,
    val doctors: OptionalInput<List<ID>> = OptionalInput.Undefined
)