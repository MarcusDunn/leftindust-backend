package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.graphql.types.GraphQLAddress
import com.leftindust.mockingbird.graphql.types.GraphQLEmail
import com.leftindust.mockingbird.graphql.types.GraphQLPhone
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput

@GraphQLName("PatientInput")
@GraphQLDescription(
    """
   The input side of Patient. Note that relations to other types are passed as ID's
   if you want to clear a list, pass an empty list, explicitly setting a list to null or leaving blank will have no effect on
   update operations and will result in empty list for create operations.
   """
)
data class GraphQLPatientInput(
    @GraphQLDescription("do not include when adding patient")
    val pid: OptionalInput<ID> = OptionalInput.Undefined,
    @GraphQLDescription("required on adding patient")
    val firstName: OptionalInput<String> = OptionalInput.Undefined,
    val middleName: OptionalInput<String?> = OptionalInput.Undefined,
    @GraphQLDescription("required on adding patient")
    val lastName: OptionalInput<String> = OptionalInput.Undefined,
    val phoneNumbers: List<GraphQLPhone>? = null, // TODO: 2021-04-27 allow passing null or leaving blank
    @GraphQLDescription("required on adding patient")
    val dateOfBirth: OptionalInput<GraphQLTimeInput> = OptionalInput.Undefined,
    val addresses: List<GraphQLAddress>? = null, // TODO: 2021-04-27 allow passing null or leaving blank
    val emails: List<GraphQLEmail>? = null, // TODO: 2021-04-27 allow passing null or leaving blank
    val insuranceNumber: OptionalInput<ID> = OptionalInput.Undefined,
    val sex: OptionalInput<Sex> = OptionalInput.Undefined,
    val gender: OptionalInput<String> = OptionalInput.Undefined,
    val ethnicity: OptionalInput<Ethnicity> = OptionalInput.Undefined,
    val emergencyContact: List<GraphQLEmergencyContactInput>? = null,
    val doctors: List<ID>? = null, // TODO: 2021-04-27 allow passing null or leaving blank
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