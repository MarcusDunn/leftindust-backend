package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import org.springframework.stereotype.Component

@Component
class PatientMutation(
    private val patientDao: PatientDao,
) : Mutation {
    suspend fun addDoctorToPatient(patientById: ID, doctorById: ID, authContext: GraphQLAuthContext): GraphQLPatient {
        return patientDao.addDoctorToPatient(patientById, doctorById, authContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLPatient(it, it.id!!, authContext) } // safe nn assert as we just got from DB
    }

    @GraphQLDescription("updates a patient by their pid, only the not null fields are updated, pid MUST be defined")
    suspend fun updatePatient(patient: GraphQLPatientInput, graphQLAuthContext: GraphQLAuthContext): GraphQLPatient {
        return patientDao
            .update(patient, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLPatient(it, it.id!!, graphQLAuthContext) } // safe nn assert as we just got from DB
    }

    @GraphQLDescription(
        """adds a new patient and connects them to already existing doctors and contacts
        contacts and doctors default to empty lists"""
    )
    suspend fun addPatient(patient: GraphQLPatientInput, graphQLAuthContext: GraphQLAuthContext): GraphQLPatient {
        return patientDao
            .addNewPatient(
                patient = patient,
                requester = graphQLAuthContext.mediqAuthToken
            )
            .getOrThrow()
            .let { GraphQLPatient(it, it.id!!, graphQLAuthContext) }  // safe nn assert as we just added from DB
    }
}