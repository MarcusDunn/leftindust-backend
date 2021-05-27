package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput

interface VisitDao {
    suspend fun getVisitByVid(vid: GraphQLVisit.ID, requester: MediqToken): Visit
    suspend fun addVisit(visitInput: GraphQLVisitInput, requester: MediqToken): Visit
    suspend fun getByEvent(eid: GraphQLEvent.ID, requester: MediqToken): Visit
    suspend fun getByPatient(pid: GraphQLPatient.ID, requester: MediqToken): List<Visit>
}