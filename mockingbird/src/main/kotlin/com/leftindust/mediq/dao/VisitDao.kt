package com.leftindust.mediq.dao

import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.entity.Visit
import com.leftindust.mediq.extensions.CustomResult
import com.leftindust.mediq.graphql.types.GraphQLVisitInput

interface VisitDao {
    suspend fun getVisitsForPatientPid(pid: Int, requester: MediqToken): CustomResult<List<Visit>, OrmFailureReason>
    suspend fun getVisitByVid(vid: Long, requester: MediqToken): CustomResult<Visit, OrmFailureReason>
    suspend fun getVisitsByDoctor(did: Long, requester: MediqToken): CustomResult<List<Visit>, OrmFailureReason>
    suspend fun addVisit(visitInput: GraphQLVisitInput, requester: MediqToken): CustomResult<Visit, OrmFailureReason>
}