package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.graphql.types.input.GraphQLDateInput
import java.sql.Timestamp
import java.time.*
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

    constructor(instant: Instant) : this(
        unixMilliseconds = instant.toEpochMilli()
    )

    constructor(timestamp: Timestamp) : this(timestamp.toInstant())
}


data class GraphQLDate(
    val day: Int,
    val month: GraphQLMonth,
    val year: Int,
) {
    constructor(date: LocalDate) : this(
        day = date.dayOfMonth,
        month = GraphQLMonth.fromInt(date.monthValue - 1)
            ?: throw RuntimeException("could not transform ${date.month} into a GraphQLnth"),
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
        fun fromInt(month: Int): GraphQLMonth? = values().getOrNull(month)
    }
}

@GraphQLName("TimeInput")
@GraphQLDescription("Sum type over time or date")
data class GraphQLTimeInput(
    val time: GraphQLUtcTime? = null,
    val date: GraphQLDateInput? = null
) {
    constructor(time: Timestamp) : this(time = GraphQLUtcTime(time), date = null)

    init { // validates sum typeyness
        if ((time == null) xor (date == null)) {
            // valid input
        } else {
            throw IllegalArgumentException("TimeInput cannot contain both time or date or neither")
        }
    }

    fun toTimestamp(): Timestamp = time?.toTimestamp() ?: date!!.toTimeStamp()

    fun before(other: GraphQLTimeInput): Boolean {
        return toTimestamp().time < other.toTimestamp().time
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