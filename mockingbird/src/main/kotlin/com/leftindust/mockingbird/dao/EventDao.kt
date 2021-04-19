package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.mutations.EventMutation
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLTimeRangeInput

interface EventDao {
    suspend fun addEvent(
        event: GraphQLEventInput,
        requester: MediqToken
    ): CustomResult<Event, OrmFailureReason>

    suspend fun getMany(
        range: GraphQLTimeRangeInput,
        requester: MediqToken
    ): Collection<Event>

    suspend fun getById(eid: ID, requester: MediqToken): CustomResult<Event, OrmFailureReason>

    suspend fun getByPatient(pid: Long, requester: MediqToken): Collection<Event>

    suspend fun getByDoctor(did: Long, requester: MediqToken): Collection<Event>

    suspend fun getByVisit(vid: Long, requester: MediqToken): Event

    suspend fun editEvent(
        event: GraphQLEventEditInput,
        requester: MediqToken,
    ): Event

    suspend fun editRecurringEvent(
        event: GraphQLEventEditInput,
        requester: MediqToken,
        recurrenceSettings: EventMutation.GraphQLRecurrenceEditSettings
    ): Event
}
