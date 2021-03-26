package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.execution.OptionalInput
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.graphql.types.*

@GraphQLName("PatientInput")
data class GraphQLPatientInput(
    val pid: OptionalInput<ID> = OptionalInput.Undefined,
    val firstName: OptionalInput<String> = OptionalInput.Undefined,
    val middleName: OptionalInput<String?> = OptionalInput.Undefined,
    val lastName: OptionalInput<String> = OptionalInput.Undefined,
    val phoneNumbers: OptionalInput<List<GraphQLPhoneNumber>> = OptionalInput.Undefined,
    val dateOfBirth: OptionalInput<GraphQLTimeInput> = OptionalInput.Undefined,
    val addresses: OptionalInput<List<GraphQLAddress>> = OptionalInput.Undefined,
    val emails: OptionalInput<List<GraphQLEmail>> = OptionalInput.Undefined,
    val insuranceNumber: OptionalInput<ID> = OptionalInput.Undefined,
    val sex: OptionalInput<Sex> = OptionalInput.Undefined,
    val gender: OptionalInput<String> = OptionalInput.Undefined,
    val ethnicity: OptionalInput<Ethnicity> = OptionalInput.Undefined,
    val emergencyContact: OptionalInput<List<GraphQLEmergencyContact>> = OptionalInput.Undefined,
    val doctors: OptionalInput<List<ID>> = OptionalInput.Undefined,
) {
    init {
        if (emails is OptionalInput.Defined) {
            if (emails.value?.all { it.email.contains("@") } == true) {
                // valid email
            } else {
                throw IllegalArgumentException("not a valid email")
            }
        }
    }
}