package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.gqlID
import org.springframework.beans.factory.annotation.Autowired

open class GraphQLEvent(
    val eid: ID,
    val title: String,
    val description: String?,
    val start: GraphQLTime,
    val end: GraphQLTime,
    private val graphQLAuthContext: GraphQLAuthContext
) {
    constructor(event: Event, id: Long, graphQLAuthContext: GraphQLAuthContext) : this(
        eid = gqlID(id),
        title = event.title,
        description = event.description,
        start = GraphQLTime(event.startTime),
        end = GraphQLTime(event.startTime.time + event.durationMillis),
        graphQLAuthContext = graphQLAuthContext,
    ) {
        if (event.id != id) throw RuntimeException("inconsistency between event.id and id (visit.id: ${event.id} id: $id)")
    }

    suspend fun doctor(@GraphQLIgnore @Autowired eventDao: EventDao): GraphQLDoctor? {
        TODO(eventDao.toString())
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