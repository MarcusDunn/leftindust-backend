package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import org.springframework.stereotype.Component

@Component
class PatientMutation(
    private val patientDao: PatientDao,
) : Mutation {

    @GraphQLDescription("updates a patient by their pid, only the not null fields are updated, pid MUST be defined")
    suspend fun updatePatient(patient: GraphQLPatientEditInput, graphQLAuthContext: GraphQLAuthContext): GraphQLPatient {
        return patientDao
            .update(patient, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLPatient(it, graphQLAuthContext) } // safe nn assert as we just got from DB
    }

    @GraphQLDescription(
        """adds a new patient and connects them to already existing doctors and contacts
        contacts and doctors default to empty lists"""
    )
    suspend fun addPatient(patient: GraphQLPatientInput, graphQLAuthContext: GraphQLAuthContext): GraphQLPatient {
        return patientDao
            .addNewPatient(patient, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLPatient(it, graphQLAuthContext) }
    }
}