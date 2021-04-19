package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput

/**
 * handles getting and authorizing of all database requests involving the patients
 */
interface PatientDao {
    suspend fun getByPID(pID: Long, requester: MediqToken): Patient
    suspend fun addNewPatient(
        patient: GraphQLPatientInput,
        requester: MediqToken
    ): Patient

    suspend fun removeByPID(pid: Long, requester: MediqToken): Patient
    suspend fun getByDoctor(did: Long, requester: MediqToken): Collection<Patient>
    suspend fun getByVisit(vid: Long, requester: MediqToken): Collection<Patient>
    suspend fun addDoctorToPatient(
        patientInput: ID,
        doctorInput: ID,
        requester: MediqToken
    ): Patient

    suspend fun searchByExample(
        example: GraphQLPatientExample,
        requester: MediqToken,
        strict: Boolean = true,
    ): List<Patient>

    suspend fun getMany(
        from: Int,
        to: Int,
        sortedBy: Patient.SortableField = Patient.SortableField.PID,
        requester: MediqToken
    ): Collection<Patient>

    suspend fun update(
        patientInput: GraphQLPatientInput,
        requester: MediqToken
    ): Patient

    suspend fun getByEvent(eid: Long, mediqAuthToken: MediqToken): Collection<Patient> {
        TODO("Not yet implemented")
    }
}
