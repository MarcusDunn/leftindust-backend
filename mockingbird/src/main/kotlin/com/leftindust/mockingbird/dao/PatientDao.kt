package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput

/**
 * handles getting and authorizing of all database requests involving the patients
 */
interface PatientDao {
    /**
     * gets a single patient by pID
     * @param pID how the patient is searched for
     * @param requester the token of the user making the request
     */
    suspend fun getByPID(pID: Long, requester: MediqToken): CustomResult<Patient, OrmFailureReason>

    /**
     * gets multiple patients from the DB, always tries to return max, but does not that many
     * @param from pagination start (inclusive)
     * @param to pagination end (exclusive)
     * @param requester the token of the user making the request
     */
    suspend fun getManyGroupedBySorted(
        from: Int,
        to: Int,
        sortedBy: Patient.SortableField,
        requester: MediqToken,
    ): CustomResult<Map<String, List<Patient>>, OrmFailureReason>

    /**
     * adds a new patient to the Database. fails if a patient with that pID already exists
     * @param patient the new patient to be added
     * @param requester the token of the user making the request
     */
    suspend fun addNewPatient(patient: Patient, requester: MediqToken): CustomResult<Patient, OrmFailureReason>
    suspend fun addNewPatient(
        patient: GraphQLPatientInput,
        doctorIds: List<ID> = emptyList(),
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason>

    /**
     * removes a patient from the Database. fails if no patient with that pID exists
     * @param pid the target patient's pID to be removed
     * @param requester the token of the user making the request
     */
    suspend fun removePatientByPID(pid: Long, requester: MediqToken): CustomResult<Patient, OrmFailureReason>
    suspend fun searchByName(query: String, requester: MediqToken): CustomResult<List<Patient>, OrmFailureReason>
    suspend fun getByDoctor(did: Long, requester: MediqToken): CustomResult<List<Patient>, OrmFailureReason>
    suspend fun getByVisit(vid: Long?, requester: MediqToken): CustomResult<Patient, OrmFailureReason>
    suspend fun addDoctorToPatient(
        patientInput: ID,
        doctorInput: ID,
        requester: MediqToken
    ): CustomResult<Patient, OrmFailureReason>

    suspend fun searchByExample(
        example: GraphQLPatientExample,
        requester: MediqToken,
        strict: Boolean = true,
    ): CustomResult<List<Patient>, OrmFailureReason>

    suspend fun getMany(
        from: Int,
        to: Int,
        sortedBy: Patient.SortableField,
        requester: MediqToken
    ): CustomResult<List<Patient>, OrmFailureReason>

    suspend fun update(patientInput: GraphQLPatientInput, requester: MediqToken): CustomResult<Patient, OrmFailureReason>
}
