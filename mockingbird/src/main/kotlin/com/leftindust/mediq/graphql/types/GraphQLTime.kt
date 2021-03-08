package com.leftindust.mediq.graphql.types

import biweekly.util.ICalDate
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@GraphQLName("UtcTime")
data class GraphQLTime(
    val unixSeconds: Long,
    val unixMilliseconds: Long,
) {

    @GraphQLName("TimeZonedTime")
    data class TimeZonedTime(
        val timeZone: String,
        val unixSeconds: Long,
        val unixMilliseconds: Long,
    )

    fun withRespectTo(timeZone: String): TimeZonedTime {
        val utc = ZoneId.of("UTC")
        val otherTimeZone = ZoneId.of(timeZone)
        val dateTime = Timestamp(unixMilliseconds).toLocalDateTime()
        val panamaDateTime = ZonedDateTime.of(dateTime, utc)
        val taipeiDateTime = panamaDateTime.withZoneSameInstant(otherTimeZone)

        val diffSeconds = taipeiDateTime.offset.totalSeconds.toLong()
        return TimeZonedTime(
            timeZone,
            diffSeconds,
            diffSeconds * 1000
        )
    }

    @GraphQLIgnore
    fun toTimestamp() = Timestamp(unixMilliseconds)

    constructor(instant: Instant) : this(
        unixSeconds = instant.epochSecond,
        unixMilliseconds = instant.toEpochMilli()
    )

    constructor(timestamp: Timestamp) : this(timestamp.toInstant())

    constructor(iCalDate: ICalDate) : this(iCalDate.toInstant())
}