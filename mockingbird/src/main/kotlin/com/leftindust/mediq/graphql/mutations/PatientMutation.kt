package com.leftindust.mediq.graphql.mutations

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.PatientDao
import com.leftindust.mediq.graphql.types.GraphQLPatient
import com.leftindust.mediq.graphql.types.input.GraphQLPatientInput
import org.springframework.stereotype.Component

@Component
class PatientMutation(
    val patientDao: PatientDao,
) : Mutation {
    suspend fun addDoctorToPatient(patientById: ID, doctorById: ID, authContext: GraphQLAuthContext): GraphQLPatient {
        return patientDao.addDoctorToPatient(patientById, doctorById, authContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLPatient(it, authContext) }
    }

    @GraphQLDescription("updates a patient by their pid, only the not null fields are updated")
    suspend fun updatePatient(patient: GraphQLPatientInput, graphQLAuthContext: GraphQLAuthContext): GraphQLPatient {
        return patientDao
            .update(patient, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLPatient(it, graphQLAuthContext) }
    }

    @GraphQLDescription(
        """adds a new patient and connects them to already existing doctors and contacts
        contacts and doctors default to empty lists"""
    )
    suspend fun addPatient(
        patient: GraphQLPatientInput,
        doctors: List<ID>? = emptyList(),
        graphQLAuthContext: GraphQLAuthContext
    ): GraphQLPatient {
        return patientDao
            .addNewPatient(
                patient = patient,
                doctorIds = doctors ?: emptyList(),
                requester = graphQLAuthContext.mediqAuthToken
            )
            .getOrThrow()
            .let { GraphQLPatient(it, graphQLAuthContext) }
    }
}