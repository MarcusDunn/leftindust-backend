package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.graphql.types.search.example.GraphQLPatientExample

/**
 * handles getting and authorizing of all database requests involving the patients
 */
interface PatientDao {
    suspend fun getByPID(pid: GraphQLPatient.ID, requester: MediqToken): Patient
    suspend fun addNewPatient(
        patient: GraphQLPatientInput,
        requester: MediqToken
    ): Patient

    suspend fun removeByPID(pid: GraphQLPatient.ID, requester: MediqToken): Patient
    suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Collection<Patient>
    suspend fun getByVisit(vid: GraphQLVisit.ID, requester: MediqToken): Collection<Patient>
    suspend fun addDoctorToPatient(
        pid: GraphQLPatient.ID,
        did: GraphQLDoctor.ID,
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

    suspend fun getByEvent(eid: GraphQLEvent.ID, requester: MediqToken): Collection<Patient>

    suspend fun getPatientsByPids(pids: List<GraphQLPatient.ID>, requester: MediqToken): Collection<Patient>

    suspend fun searchByExample(example: GraphQLPatientExample, requester: MediqToken): Collection<Patient>

    suspend fun getByUser(uid: String, requester: MediqToken): Patient?
    suspend fun assignForms(
        patients: List<GraphQLPatient.ID>,
        survey: GraphQLFormTemplate.ID,
        requester: MediqToken
    ): Collection<Patient>
}
