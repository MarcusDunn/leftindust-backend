package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput

interface VisitDao {
    suspend fun getVisitByVid(vid: GraphQLVisit.ID, requester: MediqToken): Visit
    suspend fun addVisit(visitInput: GraphQLVisitInput, requester: MediqToken): Visit
    suspend fun findByEvent(eid: GraphQLEvent.ID, requester: MediqToken): Visit?
    suspend fun getPatientVisits(pid: GraphQLPatient.ID, requester: MediqToken): List<Visit>
    suspend fun editVisit(visit: GraphQLVisitEditInput, requester: MediqToken): Visit
}