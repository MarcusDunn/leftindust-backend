package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput

interface EventDao {
    suspend fun addEvent(
        event: GraphQLEventInput,
        requester: MediqToken
    ): CustomResult<Event, OrmFailureReason>
}
