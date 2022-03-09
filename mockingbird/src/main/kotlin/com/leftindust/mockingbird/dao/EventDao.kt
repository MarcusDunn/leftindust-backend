package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRecurrenceEditSettings
import com.leftindust.mockingbird.graphql.types.input.GraphQLTimeRangeInput

interface EventDao {
    suspend fun addEvent(
        event: GraphQLEventInput,
        requester: MediqToken
    ): Event

    suspend fun getById(eid: GraphQLEvent.ID, requester: MediqToken): Event

    suspend fun getPatientEvents(pid: GraphQLPatient.ID, requester: MediqToken): Collection<Event>

    suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Collection<Event>

    suspend fun getEventVisit(vid: GraphQLVisit.ID, requester: MediqToken): Event

    suspend fun editEvent(
        event: GraphQLEventEditInput,
        requester: MediqToken,
    ): Event

    suspend fun editRecurringEvent(
        event: GraphQLEventEditInput,
        requester: MediqToken,
        recurrenceSettings: GraphQLRecurrenceEditSettings
    ): Event

    suspend fun getBetween(range: GraphQLTimeRangeInput, requester: MediqToken): Collection<Event>
}
