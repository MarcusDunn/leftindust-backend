package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.examples.GraphQLVisitExample

interface VisitDao {
    suspend fun getVisitByVid(vid: Long, requester: MediqToken): Visit
    suspend fun addVisit(visitInput: GraphQLVisitInput, requester: MediqToken): Visit
    suspend fun getByExample(
        example: GraphQLVisitExample,
        strict: Boolean = true,
        requester: MediqToken
    ): List<Visit>

    suspend fun getByEvent(id: Long, requester: MediqToken): Visit

    suspend fun getByPatient(pid: Long, requester: MediqToken): List<Visit>
}