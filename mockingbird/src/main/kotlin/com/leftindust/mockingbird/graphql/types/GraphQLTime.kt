package com.leftindust.mockingbird.graphql.types

import biweekly.util.ICalDate
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import java.sql.Timestamp
import java.time.*

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


data class GraphQLDate(
    val day: Int,
    val month: GraphQLMonth,
    val year: Int,
) {
    @GraphQLIgnore
    fun toInstant(): Instant = LocalDate.of(year, month.toJavaMonth(), day).atStartOfDay(ZoneId.of("UTC")).toInstant()

    @GraphQLIgnore
    fun toTimestamp(): Timestamp =
        Timestamp.from(LocalDate.of(year, month.toJavaMonth(), day).atStartOfDay(ZoneId.of("UTC")).toInstant())
}

@GraphQLName("Month")
enum class GraphQLMonth {
    Jan,
    Feb,
    Mar,
    Apr,
    May,
    Jun,
    Jul,
    Aug,
    Sep,
    Oct,
    Nov,
    Dec;

    fun toJavaMonth(): Month = when (this) {
        Jan -> Month.JANUARY
        Feb -> Month.FEBRUARY
        Mar -> Month.MARCH
        Apr -> Month.APRIL
        May -> Month.MAY
        Jun -> Month.JUNE
        Jul -> Month.JULY
        Aug -> Month.AUGUST
        Sep -> Month.SEPTEMBER
        Oct -> Month.OCTOBER
        Nov -> Month.NOVEMBER
        Dec -> Month.DECEMBER
    }
}

@GraphQLName("Time Input")
@GraphQLDescription("Sum type over time or date")
data class GraphQLTimeInput(
    val time: GraphQLTime? = null,
    val date: GraphQLDate? = null
) {
    constructor(time: Timestamp) : this(time = GraphQLTime(time), date = null)

    init { // validates sum typeyness
        if ((time == null) xor (date == null)) {
            // valid input
        } else {
            throw IllegalArgumentException("TimeInput cannot contain both time or date or neitherr")
        }
    }

    fun toTimestamp(): Timestamp = time?.toTimestamp() ?: date!!.toTimestamp()
}