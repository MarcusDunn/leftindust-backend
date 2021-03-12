package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput

interface VisitDao {
    suspend fun getVisitsForPatientPid(pid: Long, requester: MediqToken): CustomResult<List<Visit>, OrmFailureReason>
    suspend fun getVisitByVid(vid: Long, requester: MediqToken): CustomResult<Visit, OrmFailureReason>
    suspend fun getVisitsByDoctor(did: Long, requester: MediqToken): CustomResult<List<Visit>, OrmFailureReason>
    suspend fun addVisit(visitInput: GraphQLVisitInput, requester: MediqToken): CustomResult<Visit, OrmFailureReason>
}