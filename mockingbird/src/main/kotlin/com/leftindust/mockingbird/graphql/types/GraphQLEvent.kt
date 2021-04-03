package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.gqlID

data class GraphQLEvent(
    val eid: ID,
    val title: String,
    val description: String?,
    val start: GraphQLTime,
    val end: GraphQLTime,
    private val authContext: GraphQLAuthContext,
) {
    constructor(event: Event, id: Long, authContext: GraphQLAuthContext) : this(
        eid = gqlID(id),
        title = event.title,
        description = event.description,
        start = GraphQLTime(event.startTime),
        end = GraphQLTime(event.startTime.time + event.durationMillis),
        authContext = authContext
    ){
        if (event.id != id) throw RuntimeException("inconsistency between event.id and id (visit.id: ${event.id} id: $id)")
    }
}