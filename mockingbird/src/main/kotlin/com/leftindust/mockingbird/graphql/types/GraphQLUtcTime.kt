package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@GraphQLName("UtcTime")
data class GraphQLUtcTime(
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
        eg. America/Los_Angeles for British Columbia's time zone (generally referred to as PST)
        """
    )
    // this function makes ZERO sense. I have no clue why it works, the variable names are lies and im so sorry
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
    fun before(end: GraphQLUtcTime): Boolean {
        return this.toTimestamp().before(end.toTimestamp())
    }

    constructor(instant: Instant) : this(
        unixMilliseconds = instant.toEpochMilli()
    )

    constructor(timestamp: Timestamp) : this(timestamp.toInstant())
}

@GraphQLName("Date")
data class GraphQLDate(
    val day: Int,
    val month: GraphQLMonth,
    val year: Int,
) {
    constructor(date: LocalDate) : this(
        day = date.dayOfMonth,
        month = GraphQLMonth.fromJavaMonth(date.month),
        year = date.year
    )

    @GraphQLIgnore
    fun toLocalDate(): LocalDate {
        return LocalDate.of(year, month.toJavaMonth(), day)
    }

    @Deprecated("will be removed before 1.0", ReplaceWith("using the information I send you"))
    fun toUtcTime(): GraphQLUtcTime {
        return GraphQLUtcTime(Calendar.getInstance().apply { set(year, month.toJavaMonth().value, day) }.timeInMillis)
    }
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

    @GraphQLIgnore
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

    companion object {
        fun fromJavaMonth(month: Month): GraphQLMonth = when (month) {
            Month.JANUARY -> Jan
            Month.FEBRUARY -> Feb
            Month.MARCH -> Mar
            Month.APRIL -> Apr
            Month.MAY -> May
            Month.JUNE -> Jun
            Month.JULY -> Jul
            Month.AUGUST -> Aug
            Month.SEPTEMBER -> Sep
            Month.OCTOBER -> Oct
            Month.NOVEMBER -> Nov
            Month.DECEMBER -> Dec
        }
    }
}

@GraphQLName("DayOfWeek")
enum class GraphQLDayOfWeek {
    Mon,
    Tue,
    Wed,
    Thu,
    Fri,
    Sat,
    Sun;
}