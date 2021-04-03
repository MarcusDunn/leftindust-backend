package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.gqlID

open class GraphQLEvent(
    val eid: ID,
    val title: String,
    val description: String?,
    val start: GraphQLTime,
    val end: GraphQLTime,
) {
    constructor(event: Event, id: Long) : this(
        eid = gqlID(id),
        title = event.title,
        description = event.description,
        start = GraphQLTime(event.startTime),
        end = GraphQLTime(event.startTime.time + event.durationMillis),
    ) {
        if (event.id != id) throw RuntimeException("inconsistency between event.id and id (visit.id: ${event.id} id: $id)")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GraphQLEvent

        if (eid != other.eid) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (start != other.start) return false
        if (end != other.end) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }


}