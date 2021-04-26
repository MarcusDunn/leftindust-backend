package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput

interface VisitDao {
    suspend fun getVisitByVid(vid: Long, requester: MediqToken): Visit
    suspend fun addVisit(visitInput: GraphQLVisitInput, requester: MediqToken): Visit

    suspend fun getByEvent(id: Long, requester: MediqToken): Visit

    suspend fun getByPatient(pid: Long, requester: MediqToken): List<Visit>
}