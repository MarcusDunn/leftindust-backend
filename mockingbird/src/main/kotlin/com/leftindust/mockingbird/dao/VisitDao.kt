package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.examples.GraphQLVisitExample

interface VisitDao {
    suspend fun getVisitByVid(vid: Long, requester: MediqToken): CustomResult<Visit, OrmFailureReason>
    suspend fun addVisit(visitInput: GraphQLVisitInput, requester: MediqToken): CustomResult<Visit, OrmFailureReason>
    suspend fun getByExample(
        example: GraphQLVisitExample,
        strict: Boolean = true,
        requester: MediqToken
    ): CustomResult<List<Visit>, OrmFailureReason>

    suspend fun getByEvent(id: Long, requester: MediqToken): Visit

    suspend fun getByPatient(pid: Long, requester: MediqToken): List<Visit>
}