package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.entity.Event
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLName("Event")
sealed class GraphQLEvent(
    val title: String,
    val description: String?,
    val startTime: GraphQLTime,
    val endTime: GraphQLTime
) {
    @GraphQLName("UnownedEvent")
    class Unowned(
        title: String,
        description: String?,
        startTime: GraphQLTime,
        endTime: GraphQLTime
    ) : GraphQLEvent(title, description, startTime, endTime) {
        constructor(event: Event) : this(
            title = event.title,
            description = event.description,
            startTime = GraphQLTime(event.startTime),
            endTime = GraphQLTime(event.startTime.time + event.durationMillis),
        )

        constructor(event: Event, date: Date) : this(
            title = event.title,
            description = event.description,
            startTime = GraphQLTime(date.time),
            endTime = GraphQLTime(date.time + event.durationMillis),
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false
            return true
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    @GraphQLName("DoctorEvent")
    class Doctor(
        title: String,
        description: String?,
        startTime: GraphQLTime,
        endTime: GraphQLTime,
        private val doctorID: ID,
        private val graphQLAuthContext: GraphQLAuthContext,
    ) : GraphQLEvent(title, description, startTime, endTime) {
        constructor(event: Event, doctorID: ID, graphQLAuthContext: GraphQLAuthContext) : this(
            event = Unowned(event),
            doctorID = doctorID,
            graphQLAuthContext = graphQLAuthContext
        )

        constructor(event: GraphQLEvent, doctorID: ID, graphQLAuthContext: GraphQLAuthContext) : this(
            title = event.title,
            description = event.description,
            startTime = event.startTime,
            endTime = event.endTime,
            doctorID = doctorID,
            graphQLAuthContext = graphQLAuthContext
        )

        fun doctor(@GraphQLIgnore @Autowired doctorDao: DoctorDao): GraphQLDoctor {
            TODO(doctorDao.toString())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as Doctor

            if (doctorID != other.doctorID) return false
            if (graphQLAuthContext != other.graphQLAuthContext) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + doctorID.hashCode()
            result = 31 * result + graphQLAuthContext.hashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GraphQLEvent

        if (title != other.title) return false
        if (description != other.description) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        return result
    }


}