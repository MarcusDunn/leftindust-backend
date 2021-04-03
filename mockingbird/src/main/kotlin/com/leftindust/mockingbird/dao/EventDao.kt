package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput

interface EventDao {
    fun addEvent(
        event: GraphQLEventInput,
        graphQLAuthContext: GraphQLAuthContext
    ): CustomResult<Event, OrmFailureReason>
}
