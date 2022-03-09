package com.leftindust.mockingbird.dao.patient

import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.GuardedDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.graphql.types.search.example.GraphQLPatientExample

interface ReadPatientDao : GuardedDao {
    companion object {
        val necessaryPermissions = setOf(Action(Crud.READ to Tables.Patient))
    }

    override fun necessaryPermissions() = necessaryPermissions
    suspend fun getByPID(pid: GraphQLPatient.ID, requester: MediqToken): Patient
    suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Collection<Patient>
    suspend fun getVisitPatients(vid: GraphQLVisit.ID, requester: MediqToken): Collection<Patient>
    suspend fun getMany(range: GraphQLRangeInput, sortedBy: Patient.SortableField = Patient.SortableField.PID, requester: MediqToken): Collection<Patient>
    suspend fun getByEvent(eid: GraphQLEvent.ID, requester: MediqToken): Collection<Patient>
    suspend fun getPatientsByPids(pids: List<GraphQLPatient.ID>, requester: MediqToken): Collection<Patient>
    suspend fun searchByExample(example: GraphQLPatientExample, requester: MediqToken): Collection<Patient>
    suspend fun getByUser(uid: String, requester: MediqToken): Patient?
}