package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.graphql.types.*

@GraphQLName("PatientInput")
@GraphQLDescription(
    """
   The input side of Patient. Note that relations to other types are passed as ID's
   Lists are treated specially due to bug in graphql-kotlin https://github.com/ExpediaGroup/graphql-kotlin/pull/1100
   if you want to clear a list, pass an empty list, explicitly setting a list to null or leaving blank will have no effect on
   update operations and will result in empty list for create operations.
   """
)
data class GraphQLPatientInput(
    val pid: OptionalInput<ID> = OptionalInput.Undefined,
    val firstName: OptionalInput<String> = OptionalInput.Undefined,
    val middleName: OptionalInput<String?> = OptionalInput.Undefined,
    val lastName: OptionalInput<String> = OptionalInput.Undefined,
    val phoneNumbers: List<GraphQLPhone>? = null,
    val dateOfBirth: OptionalInput<GraphQLTimeInput> = OptionalInput.Undefined,
    val addresses: List<GraphQLAddress>? = null,
    val emails: List<GraphQLEmail>? = null,
    val insuranceNumber: OptionalInput<ID> = OptionalInput.Undefined,
    val sex: OptionalInput<Sex> = OptionalInput.Undefined,
    val gender: OptionalInput<String> = OptionalInput.Undefined,
    val ethnicity: OptionalInput<Ethnicity> = OptionalInput.Undefined,
    val emergencyContact: List<GraphQLEmergencyContactInput>? = null,
    val doctors: List<ID>? = null,
) {
    init {
        if (emails != null) {
            if (emails.all { it.email.contains("@") }) {
                // valid email
            } else {
                throw IllegalArgumentException("not a valid email")
            }
        }
    }
}