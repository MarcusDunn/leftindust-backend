package com.leftindust.mockingbird.graphql.types

import biweekly.util.ICalDate
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import java.sql.Timestamp
import java.time.*

@GraphQLName("UtcTime")
data class GraphQLTime(
    val unixMilliseconds: Long,
) {

    @GraphQLName("TimeZonedTime")
    data class TimeZonedTime(
        val timeZone: String,
        val unixMilliseconds: Long,
    )

    @GraphQLDescription(
        """
        the timezone string should follow the format from https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
        eg. America/Los_Angeles for British Columbias's time zone (generally referred to as PST)
        """
    )
    // this function makes ZERO sense. I have no clue why it works, the varibles names are lies and im so sorry
    fun withRespectTo(timeZone: String): TimeZonedTime {
        val utc = ZoneId.of("UTC")
        val otherTimeZone = ZoneId.of(timeZone)

        val utcDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(unixMilliseconds), otherTimeZone)
        val zonedDateTime = utcDateTime.withZoneSameLocal(utc)

        return TimeZonedTime(
            timeZone,
            zonedDateTime.toEpochSecond() * 1000,
        )
    }

    @GraphQLIgnore
    fun toTimestamp() = Timestamp(unixMilliseconds)

    constructor(instant: Instant) : this(
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

@GraphQLName("TimeInput")
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
            throw IllegalArgumentException("TimeInput cannot contain both time or date or neither")
        }
    }

    fun toTimestamp(): Timestamp = time?.toTimestamp() ?: date!!.toTimestamp()
}