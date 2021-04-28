package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.example.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput

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

    suspend fun getMany(
        range: GraphQLRangeInput,
        sortedBy: Patient.SortableField = Patient.SortableField.PID,
        requester: MediqToken
    ): Collection<Patient>

    suspend fun update(
        patientInput: GraphQLPatientEditInput,
        requester: MediqToken
    ): Patient

    suspend fun getByEvent(eid: Long, mediqAuthToken: MediqToken): Collection<Patient> {
        TODO("Not yet implemented")
    }

    suspend fun getPatientsByPids(pids: List<ID>, requester: MediqToken): Collection<Patient>
    suspend fun searchByExample(example: GraphQLPatientExample, requester: MediqToken): Collection<Patient>
}
